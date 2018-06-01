package com.rau.bot.service;

import com.github.messenger4j.Messenger;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import com.github.messenger4j.send.MessagePayload;
import com.github.messenger4j.send.MessagingType;
import com.github.messenger4j.send.SenderActionPayload;
import com.github.messenger4j.send.message.TextMessage;
import com.github.messenger4j.send.message.quickreply.QuickReply;
import com.github.messenger4j.send.message.quickreply.TextQuickReply;
import com.github.messenger4j.send.senderaction.SenderAction;
import com.github.messenger4j.webhook.event.AttachmentMessageEvent;
import com.github.messenger4j.webhook.event.PostbackEvent;
import com.github.messenger4j.webhook.event.QuickReplyMessageEvent;
import com.github.messenger4j.webhook.event.TextMessageEvent;
import com.rau.bot.dto.QuickReplyDto;
import com.rau.bot.dto.QuickReplyResponseDto;
import com.rau.bot.dto.UserStateDto;
import com.rau.bot.entity.user.*;
import com.rau.bot.enums.UserState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.rau.bot.enums.UserState.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
@Slf4j
public class MessengerService {
    private final Messenger messenger;

    @Value("${messenger4j.pageAccessToken}")
    private String pageAccessToken;

    @Value("${backend.url}")
    private String backendUrl;

    private Map<String, UserStateDto> stateMap;

    public MessengerService(@Value("${messenger4j.appSecret}") final String appSecret,
                            @Value("${messenger4j.verifyToken}") final String verifyToken,
                            @Value("${messenger4j.pageAccessToken}") final String pageAccessToken) {
        this.messenger = Messenger.create(pageAccessToken, appSecret, verifyToken);
        stateMap = new HashMap<>();
    }

    public void newQuickReplyMessageEventHandler(QuickReplyMessageEvent event) throws MessengerApiException, MessengerIOException {
        log.info("Received new Quick Reply event.");

        String userId = event.senderId();
        String payload = event.payload();

        UserStateDto userStateDto = stateMap.get(userId);
        User user = userStateDto.getUser();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        UriComponentsBuilder builder;
        HttpEntity<?> entity;
        if (payload.equals("end")) {
            stateMap.remove(userId);
            sendTextMessageToUser(userId, "Registration canceled.");
            return;
        } else if (payload.equals("back")) {
            switch (userStateDto.getUserState()) {
                case DEPARTMENT:
                    userStateDto.setUserState(ARMENIAN_SECTOR);
                    user.setArmenianSector(null);
                    break;
                case FACULTY:
                    userStateDto.setUserState(DEPARTMENT);
                    user.setFaculty(null);
                    break;
                case COURSE:
                    userStateDto.setUserState(FACULTY);
                    user.setFaculty(null);
                    break;
                case GROUP:
                    userStateDto.setUserState(COURSE);
                    user.setCourse(null);
                    break;
                case PARTITION:
                    userStateDto.setUserState(GROUP);
                    user.setGroup(null);
                    break;
                case ARMENIAN_SECTOR:
                default:
                    sendTextMessageToUser(userId, "Something went wrong ...");
                    return;
            }
        } else {
            switch (userStateDto.getUserState()) {
                case ARMENIAN_SECTOR:
                    userStateDto.setUserState(DEPARTMENT);
                    user.setArmenianSector(payload.toLowerCase().equals("true"));
                    break;
                case DEPARTMENT:
                    userStateDto.setUserState(UserState.FACULTY);
                    builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/admin/get/department/" + payload);
                    entity = new HttpEntity<>(userId, headers);

                    Department department = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, Department.class).getBody();
                    user.setFaculty(new Faculty(null, department));
                    break;
                case FACULTY:
                    userStateDto.setUserState(UserState.COURSE);
                    builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/admin/get/faculty" + payload);
                    entity = new HttpEntity<>(userId, headers);

                    Faculty faculty = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, Faculty.class).getBody();
                    user.setFaculty(faculty);
                    break;
                case COURSE:
                    userStateDto.setUserState(GROUP);
                    builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/admin/get/course" + payload);
                    entity = new HttpEntity<>(userId, headers);

                    Course course = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, Course.class).getBody();
                    user.setCourse(course);
                    break;
                case GROUP:
                    userStateDto.setUserState(PARTITION);
                    builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/admin/get/group" + payload);
                    entity = new HttpEntity<>(userId, headers);

                    Group group = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, Group.class).getBody();
                    user.setGroup(group);
                    break;
                case PARTITION:
                    user.setFromFirstPart(payload.equals("1"));
                    builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/add");
                    entity = new HttpEntity<>(user, headers);

                    restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, Object.class).getBody();
                    sendTextMessageToUser(userId, "You have successfully registered! Now you can search your module, exams and schedule. Have a nice day!");
                    stateMap.remove(userId);
                    break;
                default:
                    return;
            }
        }
        sendNextRegistrationStep(userId);
    }

    public void sendTextMessageToUser(String userId, String text) throws MessengerApiException, MessengerIOException {
        final MessagePayload payload = MessagePayload.create(userId,
                MessagingType.RESPONSE, TextMessage.create(text));
        messenger.send(payload);
    }

    public void newTextMessageEventHandler(TextMessageEvent event) {
        log.info("Received new Text Message event.");
    }


    public void newPostbackEventHandler(PostbackEvent event) throws MessengerApiException, MessengerIOException {
        log.info("Received new Postback event.");
        Optional<String> payloadOpt = event.payload();
        String urlStr = "";
        String userId = event.senderId();

        if (payloadOpt.isPresent()) {
            switch (payloadOpt.get()) {
                case "NEXT":
                    urlStr = "/messenger/schedule/next";
                    break;
                case "TODAY":
                    urlStr = "/messenger/schedule/today";
                    break;
                case "THIS_WEEK":
                    urlStr = "/messenger/schedule/all/week";
                    break;
                case "NEXT_MODULE":
                    urlStr = "/module/next";
                    break;
                case "ALL_MODULES":
                    urlStr = "/module/all";
                    break;
                case "NEXT_EXAM":
                    urlStr = "/exam/next";
                    break;
                case "ALL_EXAMS":
                    urlStr = "/exam/all";
                    break;
                case "REGISTER":
                    if (stateMap.get(userId) == null) {
                        stateMap.put(userId, new UserStateDto(new User(), ARMENIAN_SECTOR));
                    }
                    sendNextRegistrationStep(userId);
                default:
                    return;
            }
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(backendUrl + urlStr);


            HttpEntity<?> entity = new HttpEntity<>(userId, headers);


            restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, Object.class);
        }
    }

    public void sendNextRegistrationStep(String userId) throws MessengerApiException, MessengerIOException {
        UserStateDto userStateDto = stateMap.get(userId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(userId, headers);
        UriComponentsBuilder builder;

        User user = userStateDto.getUser();

        switch (userStateDto.getUserState()) {
            case ARMENIAN_SECTOR:
                sendQuickRepliesToUser(userId, new QuickReplyResponseDto("Are you from Armenian sector?",
                        Arrays.asList(new QuickReplyDto("Yes", "true"),
                                new QuickReplyDto("No", "false"))), false);
                userStateDto.setUserState(DEPARTMENT);
                return;
            case DEPARTMENT:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/department")
                        .queryParam("fromArmenianSector", user.getArmenianSector());
                userStateDto.setUserState(UserState.FACULTY);
                break;
            case FACULTY:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/faculty/" + user.getFaculty().getDepartment().getId())
                        .queryParam("fromArmenianSector", user.getArmenianSector());
                userStateDto.setUserState(UserState.COURSE);
                break;
            case COURSE:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/course/" + user.getFaculty().getId().toString())
                        .queryParam("fromArmenianSector", user.getArmenianSector());
                userStateDto.setUserState(GROUP);
                break;
            case GROUP:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/group/"
                        + user.getFaculty().getId().toString()
                        + "/"
                        + user.getCourse().getId().toString())
                        .queryParam("fromArmenianSector", user.getArmenianSector());
                userStateDto.setUserState(PARTITION);
                break;
            case PARTITION:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/group/has/partitions/"
                        + user.getFaculty().getId().toString()
                        + "/"
                        + user.getCourse().getId().toString()
                        + "/"
                        + user.getGroup().getId().toString())
                        .queryParam("fromArmenianSector", userStateDto.getUser().getArmenianSector());
                userStateDto.setUserState(DEPARTMENT);
                break;
            default:
                sendTextMessageToUser(userId, "Something went wrong...");
                return;
        }


        QuickReplyResponseDto quickReplyResponseDto = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, QuickReplyResponseDto.class).getBody();

        sendQuickRepliesToUser(userId, quickReplyResponseDto, true);
    }


    //region Unimportant methods
    public void newAttachmentMessageEventHandler(AttachmentMessageEvent event) {
        log.info("Received new Attachment event.");
    }

    public void newReferralEventHandler() {
        log.info("Received new Referral event.");
    }

    public void newAccountLinkingEventHandler() {
        log.info("Received new AccountLinking event.");
    }

    public void newOptInEventHandler() {
        log.info("Received new OptIn event.");
    }

    public void newEchoMessageEventHandler() {
        log.info("Received new Echo Message event.");
    }

    public void newMessageDeliveredEventHandler() {
        log.info("Received new MessageDelivery event.");
    }

    public void newMessageReadEventHandler() {
        log.info("Received new MessageRead event.");
    }

    //endregion


    /**
     * Sends the given text message to the specified telegram chat
     * Method is synchronized because method call's by many threads
     *
     * @param userId current conversation id
     * @param text   message content
     */
    public synchronized void sendTextMessageToClient(String userId, String text) throws MessengerApiException, MessengerIOException {
        SenderAction typingOnAction = SenderAction.TYPING_ON;
        SenderActionPayload payloadForTypingOn = SenderActionPayload.create(userId, typingOnAction);

        SenderAction typingOffAction = SenderAction.TYPING_OFF;
        SenderActionPayload payloadForTypingOFF = SenderActionPayload.create(userId, typingOffAction);

        MessagePayload messagePayload;
        String newTextMessage;
        for (int i = 0, length = text.length(); i < length / 640 + 1; i++) {
            messenger.send(payloadForTypingOn);
            if (length - i * 640 < 640) {
                newTextMessage = text.substring(i * 640);
            } else {
                newTextMessage = text.substring(i * 640, i * 640 + 640);
            }
            messagePayload = MessagePayload.create(userId,
                    MessagingType.RESPONSE, TextMessage.create(newTextMessage));
            messenger.send(messagePayload);
        }
        messenger.send(payloadForTypingOFF);
    }

    public void sendQuickRepliesToUser(String userId, QuickReplyResponseDto quickReplyResponseDto, boolean hasExtraButtons) throws MessengerApiException, MessengerIOException {
        String text = quickReplyResponseDto.getText();
        List<QuickReplyDto> quickReplyDtoList = quickReplyResponseDto.getQuickReplyDtoList();
        if (hasExtraButtons) {
            quickReplyDtoList.add(new QuickReplyDto("Back", "back"));
            quickReplyDtoList.add(new QuickReplyDto("Cancel", "end"));
        }

        List<QuickReply> quickReplies = quickReplyDtoList.stream()
                .map(quickReplyDto -> TextQuickReply.create(quickReplyDto.getText(), quickReplyDto.getPayload()))
                .collect(Collectors.toList());

        final TextMessage message = TextMessage.create(text, of(quickReplies), empty());
        final MessagePayload payload = MessagePayload.create(userId, MessagingType.RESPONSE, message);

        messenger.send(payload);

    }
}
