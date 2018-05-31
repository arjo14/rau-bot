package com.rau.bot.messenger;


import com.github.messenger4j.Messenger;
import com.github.messenger4j.common.SupportedLocale;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import com.github.messenger4j.exception.MessengerVerificationException;
import com.github.messenger4j.messengerprofile.MessengerSettingProperty;
import com.github.messenger4j.messengerprofile.MessengerSettings;
import com.github.messenger4j.messengerprofile.getstarted.StartButton;
import com.github.messenger4j.messengerprofile.greeting.Greeting;
import com.github.messenger4j.messengerprofile.greeting.LocalizedGreeting;
import com.github.messenger4j.messengerprofile.persistentmenu.LocalizedPersistentMenu;
import com.github.messenger4j.messengerprofile.persistentmenu.PersistentMenu;
import com.github.messenger4j.messengerprofile.persistentmenu.action.NestedCallToAction;
import com.github.messenger4j.messengerprofile.persistentmenu.action.PostbackCallToAction;
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
import com.rau.bot.entity.user.Department;
import com.rau.bot.entity.user.User;
import com.rau.bot.enums.UserState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@RestController
@Slf4j
public class MessengerBot {
    private final Messenger messenger;

    @Value("${messenger4j.pageAccessToken}")
    private String pageAccessToken;

    @Value("${backend.url}")
    private String backendUrl;
    private Map<String, UserStateDto> stateMap;

    public MessengerBot(@Value("${messenger4j.appSecret}") final String appSecret,
                        @Value("${messenger4j.verifyToken}") final String verifyToken, @Value("${messenger4j.pageAccessToken}") final String pageAccessToken) throws MessengerApiException, MessengerIOException {

        this.messenger = Messenger.create(pageAccessToken, appSecret, verifyToken);
        messenger.deleteSettings(MessengerSettingProperty.PERSISTENT_MENU);

        final PostbackCallToAction callToActionAA = PostbackCallToAction.create("Next Lesson", "NEXT");
        final PostbackCallToAction callToActionAB = PostbackCallToAction.create("Today", "TODAY");
        final PostbackCallToAction callToActionAC = PostbackCallToAction.create("This week", "THIS_WEEK");
        final NestedCallToAction callToActionForSchedule = NestedCallToAction.create("\uD83D\uDDD3️ Schedule",
                Arrays.asList(callToActionAA, callToActionAB, callToActionAC));


        final PostbackCallToAction callToActionAA1 = PostbackCallToAction.create("⏭️ Next Module", "NEXT_MODULE");
        final PostbackCallToAction callToActionAC1 = PostbackCallToAction.create("☠️ All upcoming Modules", "ALL_MODULES");

        final PostbackCallToAction callToActionAA2 = PostbackCallToAction.create("⏭️ Next Exam", "NEXT_EXAM");
        final PostbackCallToAction callToActionAC2 = PostbackCallToAction.create("☠️ All upcoming Exams", "ALL_EXAMS");

        final NestedCallToAction callToActionForModules = NestedCallToAction.create("\uD83D\uDD14 Modules",
                Arrays.asList(callToActionAA1, callToActionAC1));

        final NestedCallToAction callToActionForFinalExams = NestedCallToAction.create("\uD83D\uDD14 Exams",
                Arrays.asList(callToActionAA2, callToActionAC2));

        final NestedCallToAction callToActionForExams = NestedCallToAction.create("\uD83D\uDD14 Exams",
                Arrays.asList(callToActionForModules, callToActionForFinalExams));

        final PostbackCallToAction callToAction4 = PostbackCallToAction.create("\uD83D\uDEE0️ Register", "REGISTER");

        final Greeting greeting = Greeting.create("Hello!", LocalizedGreeting.create(SupportedLocale.en_US,
                "This is a RAU bot ! "));

        final PersistentMenu persistentMenu = PersistentMenu.create(true,
                of(Arrays.asList(callToActionForSchedule, callToActionForExams, callToAction4)),
                LocalizedPersistentMenu.create(SupportedLocale.cs_CZ, false, empty()));

        MessengerSettings messengerSettings = MessengerSettings.create(of(StartButton.create("Բարլուսիկ")), of(greeting), of(persistentMenu), empty(), empty(), empty(), empty());
        messenger.updateSettings(messengerSettings);
    }

    /**
     * Webhook verification endpoint.
     * The passed verification token (as query parameter) must match the configured verification token.
     * In case this is true, the passed challenge string must be returned by this endpoint.
     */
    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebHook(@RequestParam("hub.mode") final String mode,
                                                @RequestParam("hub.verify_token") final String verifyToken,
                                                @RequestParam("hub.challenge") final String challenge) {
        try {
            this.messenger.verifyWebhook(mode, verifyToken);
            log.info("Webhook verified successfully.");
        } catch (MessengerVerificationException ignored) {
            log.error("Can't verify webhook.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(challenge);
    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public ResponseEntity handleCallback(@RequestBody final String payload,
                                         @RequestHeader("X-Hub-Signature") final String signature) throws MessengerVerificationException {
        try {
            messenger.onReceiveEvents(payload, of(signature), event -> {
                //region Event handler
                if (event.isTextMessageEvent()) {
                    try {
                        messenger.send(getMarkSeenPayload(event.senderId()));
                    } catch (MessengerApiException | MessengerIOException e) {
                        log.error("Can't send MARK_SEEN action.");
                        e.printStackTrace();
                    }
                    newTextMessageEventHandler(event.asTextMessageEvent());
                } else if (event.isQuickReplyMessageEvent()) {
                    newQuickReplyMessageEventHandler(event.asQuickReplyMessageEvent());
                } else if (event.isAttachmentMessageEvent()) {
                    newAttachmentMessageEventHandler(event.asAttachmentMessageEvent());
                } else if (event.isPostbackEvent()) {
                    newPostbackEventHandler(event.asPostbackEvent());
                } else if (event.isMessageEchoEvent()) {
                    newEchoMessageEventHandler();
                } else if (event.isAccountLinkingEvent()) {
                    newAccountLinkingEventHandler();
                } else if (event.isMessageDeliveredEvent()) {
                    newMessageDeliveredEventHandler();
                } else if (event.isMessageReadEvent()) {
                    newMessageReadEventHandler();
                } else if (event.isOptInEvent()) {
                    newOptInEventHandler();
                } else if (event.isReferralEvent()) {
                    newReferralEventHandler();
                }
                //endregion
            });
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/send/text")
    public ResponseEntity<?> sendTextMessage(@RequestParam("userId") String userId,
                                             @RequestParam("text") String text) throws MessengerApiException, MessengerIOException {

        sendTextMessageToUser(userId, text);
        return ResponseEntity.ok().build();
    }

    private void sendTextMessageToUser(String userId, String text) throws MessengerApiException, MessengerIOException {
        final MessagePayload payload = MessagePayload.create(userId,
                MessagingType.RESPONSE, TextMessage.create(text));
        messenger.send(payload);
    }

    private void newTextMessageEventHandler(TextMessageEvent event) {
        log.info("Received new Text Message event.");
    }

    private void newQuickReplyMessageEventHandler(QuickReplyMessageEvent event) {
        log.info("Received new Quick Reply event.");

        String userId = event.senderId();
        String payload = event.payload();
        String text = event.payload().substring(2);

        UserStateDto userStateDto = stateMap.get(userId);


        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(event.senderId(), headers);
        UriComponentsBuilder builder;

        switch (payload.substring(0, 1)) {
            case "1":
                user.setArmenianSector(text.equals("true"));


                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/department")
                        .queryParam("fromArmenianSector", user.getArmenianSector());
                List<Department> departments = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, List.class).getBody();
                for (Department department : departments) {

                }
                break;
            case "2":


            case "3":

            case "4":

            case "5":
        }

        List list = new ArrayList();


    }

    private void newPostbackEventHandler(PostbackEvent event) throws MessengerApiException, MessengerIOException {
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
                        stateMap.put(userId, new UserStateDto(new User(), UserState.ARMENIAN_SECTOR));
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

    private void sendNextRegistrationStep(String userId) throws MessengerApiException, MessengerIOException {
        UserStateDto userStateDto = stateMap.get(userId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(userId, headers);
        UriComponentsBuilder builder;

        switch (userStateDto.getUserState()) {
            case ARMENIAN_SECTOR:
                sendQuickRepliesToUser(userId, new QuickReplyResponseDto("Are you from armenian sector?",
                        Arrays.asList(new QuickReplyDto("yes", "1_true"), new QuickReplyDto("no", "1_false"))), true);
                userStateDto.setUserState(UserState.DEPARTMENT);
                return;
            case DEPARTMENT:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/department")
                        .queryParam("fromArmenianSector", userStateDto.getUser().getArmenianSector());
                userStateDto.setUserState(UserState.FACULTY);
                break;
            case FACULTY:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/")
                        .queryParam("fromArmenianSector", userStateDto.getUser().getArmenianSector());
                userStateDto.setUserState(UserState.COURSE);
                break;
            case COURSE:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/")
                        .queryParam("fromArmenianSector", userStateDto.getUser().getArmenianSector());
                userStateDto.setUserState(UserState.GROUP);
                break;
            case GROUP:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/")
                        .queryParam("fromArmenianSector", userStateDto.getUser().getArmenianSector());
                userStateDto.setUserState(UserState.PARTITION);
                break;
            case PARTITION:
                builder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/user/")
                        .queryParam("fromArmenianSector", userStateDto.getUser().getArmenianSector());
                userStateDto.setUserState(UserState.DEPARTMENT);
                break;
            default:
                sendTextMessage(userId, "Something went wrong...");
                return;
        }


        QuickReplyResponseDto quickReplyResponseDto = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, QuickReplyResponseDto.class).getBody();

        sendQuickRepliesToUser(userId, quickReplyResponseDto, true);
    }


    //region Unimportant methods
    private void newAttachmentMessageEventHandler(AttachmentMessageEvent event) {
        log.info("Received new Attachment event.");
    }

    private void newReferralEventHandler() {
        log.info("Received new Referral event.");
    }

    private void newAccountLinkingEventHandler() {
        log.info("Received new AccountLinking event.");
    }

    private void newOptInEventHandler() {
        log.info("Received new OptIn event.");
    }

    private void newEchoMessageEventHandler() {
        log.info("Received new Echo Message event.");
    }

    private void newMessageDeliveredEventHandler() {
        log.info("Received new MessageDelivery event.");
    }

    private void newMessageReadEventHandler() {
        log.info("Received new MessageRead event.");
    }

    //endregion

    //region Mark Seen method

    private SenderActionPayload getMarkSeenPayload(String senderId) {
        return SenderActionPayload.create(senderId, SenderAction.MARK_SEEN);
    }

    //endregion

    /**
     * Sends the given text message to the specified telegram chat
     * Method is synchronized because method call's by many threads
     *
     * @param userId current conversation id
     * @param text   message content
     */
    private synchronized void sendTextMessageToClient(String userId, String text) throws MessengerApiException, MessengerIOException {
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

    private void sendQuickRepliesToUser(String userId, QuickReplyResponseDto quickReplyResponseDto, boolean hasExtraButtons) throws MessengerApiException, MessengerIOException {
        String text = quickReplyResponseDto.getText();
        List<QuickReplyDto> quickReplyDtoList = quickReplyResponseDto.getQuickReplyDtoList();
        if (hasExtraButtons) {
            quickReplyDtoList.add(new QuickReplyDto("Back", "8"));
            quickReplyDtoList.add(new QuickReplyDto("Cancel", "9"));
        }

        List<QuickReply> quickReplies = quickReplyDtoList.stream()
                .map(quickReplyDto -> TextQuickReply.create(quickReplyDto.getText(), quickReplyDto.getPayload()))
                .collect(Collectors.toList());

        final TextMessage message = TextMessage.create(text, of(quickReplies), empty());
        final MessagePayload payload = MessagePayload.create(userId, MessagingType.RESPONSE, message);

        messenger.send(payload);

    }

}
