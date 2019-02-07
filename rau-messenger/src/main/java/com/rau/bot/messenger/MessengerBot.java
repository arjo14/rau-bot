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
import com.rau.bot.dto.RegistrationPayload;
import com.rau.bot.service.MessengerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@RestController
@Slf4j
public class MessengerBot {
    private final MessengerService messengerService;

    public MessengerBot(MessengerService messengerService) {
        this.messengerService = messengerService;

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
        messengerService.verifyWebhook(mode,verifyToken);

        return ResponseEntity.status(HttpStatus.OK).body(challenge);
    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public ResponseEntity handleCallback(@RequestBody final String payload,
                                         @RequestHeader("X-Hub-Signature") final String signature) throws MessengerVerificationException {
        messengerService.handleCallback(payload,signature);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/send/text")
    public ResponseEntity<?> sendTextMessage(@RequestParam("userId") String userId,
                                             @RequestParam("text") String text) throws MessengerApiException, MessengerIOException {

        messengerService.sendTextMessageToUser(userId, text);
        return ResponseEntity.ok().build();
    }



}
