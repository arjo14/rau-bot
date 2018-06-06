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
import com.github.messenger4j.send.SenderActionPayload;
import com.github.messenger4j.send.senderaction.SenderAction;
import com.rau.bot.service.MessengerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@RestController
@Slf4j
public class MessengerBot {
    private final Messenger messenger;
    private final MessengerService messengerService;


    public MessengerBot(@Value("${messenger4j.appSecret}") final String appSecret,
                        @Value("${messenger4j.verifyToken}") final String verifyToken,
                        @Value("${messenger4j.pageAccessToken}") final String pageAccessToken,
                        MessengerService messengerService) throws MessengerApiException, MessengerIOException {
        this.messengerService = messengerService;

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
                    messengerService.newTextMessageEventHandler(event.asTextMessageEvent());
                } else if (event.isQuickReplyMessageEvent()) {
                    try {
                        messengerService.newQuickReplyMessageEventHandler(event.asQuickReplyMessageEvent());
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
                } else if (event.isAttachmentMessageEvent()) {
                    messengerService.newAttachmentMessageEventHandler(event.asAttachmentMessageEvent());
                } else if (event.isPostbackEvent()) {
                    try {
                        messengerService.newPostbackEventHandler(event.asPostbackEvent());
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
                } else if (event.isMessageEchoEvent()) {
                    messengerService.newEchoMessageEventHandler();
                } else if (event.isAccountLinkingEvent()) {
                    messengerService.newAccountLinkingEventHandler();
                } else if (event.isMessageDeliveredEvent()) {
                    messengerService.newMessageDeliveredEventHandler();
                } else if (event.isMessageReadEvent()) {
                    messengerService.newMessageReadEventHandler();
                } else if (event.isOptInEvent()) {
                    messengerService.newOptInEventHandler();
                } else if (event.isReferralEvent()) {
                    messengerService.newReferralEventHandler();
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

        messengerService.sendTextMessageToUser(userId, text);
        return ResponseEntity.ok().build();
    }

    //region Mark Seen method

    private SenderActionPayload getMarkSeenPayload(String senderId) {
        return SenderActionPayload.create(senderId, SenderAction.MARK_SEEN);
    }

    //endregion

}
