package com.rau.bot.service;

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
                            @Value("${messenger4j.pageAccessToken}") final String pageAccessToken) throws MessengerApiException, MessengerIOException {
        this.messenger = Messenger.create(pageAccessToken, appSecret, verifyToken);

        messenger.deleteSettings(MessengerSettingProperty.PERSISTENT_MENU);

        final PostbackCallToAction callToActionAA = PostbackCallToAction.create("След. урок", "NEXT");
        final PostbackCallToAction callToActionAB = PostbackCallToAction.create("Сегодня", "TODAY");
        final PostbackCallToAction callToActionAC = PostbackCallToAction.create("Вся неделя", "THIS_WEEK");
        final NestedCallToAction callToActionForSchedule = NestedCallToAction.create("\uD83D\uDDD3️ Расписание",
                Arrays.asList(callToActionAA, callToActionAB, callToActionAC));


        final PostbackCallToAction callToActionAA1 = PostbackCallToAction.create("⏭️ След. модуль", "NEXT_MODULE");
        final PostbackCallToAction callToActionAC1 = PostbackCallToAction.create("☠️ Все модули", "ALL_MODULES");

        final PostbackCallToAction callToActionAA2 = PostbackCallToAction.create("⏭️ След. экзамен", "NEXT_EXAM");
        final PostbackCallToAction callToActionAC2 = PostbackCallToAction.create("☠️ Все экзамены", "ALL_EXAMS");

        final NestedCallToAction callToActionForModules = NestedCallToAction.create("\uD83D\uDD14 Модули",
                Arrays.asList(callToActionAA1, callToActionAC1));

        final NestedCallToAction callToActionForFinalExams = NestedCallToAction.create("\uD83D\uDD14 Экзамены",
                Arrays.asList(callToActionAA2, callToActionAC2));

        final NestedCallToAction callToActionForExams = NestedCallToAction.create("\uD83D\uDD14 Экзамены",
                Arrays.asList(callToActionForModules, callToActionForFinalExams));

        final PostbackCallToAction callToAction4 = PostbackCallToAction.create("\uD83D\uDEE0️ Регистрация", "REGISTER");

        final Greeting greeting = Greeting.create("Hello!", LocalizedGreeting.create(SupportedLocale.en_US,
                "This is a RAU bot ! "));

        final PersistentMenu persistentMenu = PersistentMenu.create(true,
                of(Arrays.asList(callToActionForSchedule, callToActionForExams, callToAction4)),
                LocalizedPersistentMenu.create(SupportedLocale.cs_CZ, false, empty()));

        MessengerSettings messengerSettings = MessengerSettings.create(of(StartButton.create("Привет")), of(greeting), of(persistentMenu), empty(), empty(), empty(), empty());
        messenger.updateSettings(messengerSettings);
        stateMap = new HashMap<>();
    }


    public void handleCallback(String payload, String signature) {
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
                    try {
                        newQuickReplyMessageEventHandler(event.asQuickReplyMessageEvent());
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
                } else if (event.isAttachmentMessageEvent()) {
                    newAttachmentMessageEventHandler(event.asAttachmentMessageEvent());
                } else if (event.isPostbackEvent()) {
                    try {
                        newPostbackEventHandler(event.asPostbackEvent());
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
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
        } catch (IllegalArgumentException | MessengerVerificationException e) {
            log.error(e.getMessage());
        }
    }

    public void newQuickReplyMessageEventHandler(QuickReplyMessageEvent event) throws MessengerApiException, MessengerIOException {
        log.info("Received new Quick Reply event.");

        String userId = event.senderId();
        String payload = event.payload();

        switch (payload) {
            case "1":
                sendQuickRepliesToUser(userId,
                        new QuickReplyResponseDto("Хорошо! А теперь выберите институт.",
                                Arrays.asList(new QuickReplyDto("ИМИ", "2"), new QuickReplyDto("ИПП", "ипп"),
                                        new QuickReplyDto("ИГН", "игн"), new QuickReplyDto(" ИнЭкБиз", "inekbiz"))),
                        true);
                break;
            case "2":
                sendQuickRepliesToUser(userId,
                        new QuickReplyResponseDto("Отлично! Выберите факультет.",
                                Arrays.asList(new QuickReplyDto("ПМИ", "3"), new QuickReplyDto("ФизТех", "123132"),
                                        new QuickReplyDto("Биология", "5451"))),
                        true);
                break;
            case "3":
                sendQuickRepliesToUser(userId,
                        new QuickReplyResponseDto("Чуть-чуть и всё. Выберите группу.",
                                Arrays.asList(new QuickReplyDto("1", "45451"), new QuickReplyDto("2", "54351"),
                                        new QuickReplyDto("3", "4"), new QuickReplyDto("4", "5451,1"))),
                        true);
                break;
            case "4":
                sendQuickRepliesToUser(userId,
                        new QuickReplyResponseDto("Эта группа имеет уроки, которые разделяются.Выберите вашу часть",
                                Arrays.asList(new QuickReplyDto("1", "4дсвд"), new QuickReplyDto("2", "5"))),
                        true);
                break;
            case "5":
                sendTextMessageToUser(userId, "Вы успешно прошли регистрацию. Можете приступить");
                break;

            default:
                sendTextMessageToUser(userId, "У меня только тестовая дата.");
        }

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
                    sendQuickRepliesToUser(userId,
                            new QuickReplyResponseDto("Ты из армянского сектора?", Arrays.asList(new QuickReplyDto("Да", "0"), new QuickReplyDto("Нет", "1"))),
                            false);
                    return;
                case "Привет":
                    sendTextMessageToUser(userId, "Привет! Я бот для РАУ.");
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
        List<QuickReplyDto> quickReplyDtoList = new ArrayList<>(quickReplyResponseDto.getQuickReplyDtoList());
        if (hasExtraButtons) {
            quickReplyDtoList.add(new QuickReplyDto("Назад", "back"));
            quickReplyDtoList.add(new QuickReplyDto("Завершить", "end"));
        }

        List<QuickReply> quickReplies = quickReplyDtoList.stream()
                .map(quickReplyDto -> TextQuickReply.create(quickReplyDto.getText(), quickReplyDto.getPayload()))
                .collect(Collectors.toList());

        final TextMessage message = TextMessage.create(text, of(quickReplies), empty());
        final MessagePayload payload = MessagePayload.create(userId, MessagingType.RESPONSE, message);

        messenger.send(payload);

    }

    public void verifyWebhook(String mode, String verifyToken) {
        try {
            this.messenger.verifyWebhook(mode, verifyToken);
            log.info("Webhook verified successfully.");
        } catch (MessengerVerificationException ignored) {
            log.error("Can't verify webhook.");
        }
    }


    //region Mark Seen method

    private SenderActionPayload getMarkSeenPayload(String senderId) {
        return SenderActionPayload.create(senderId, SenderAction.MARK_SEEN);
    }

    //endregion
}
