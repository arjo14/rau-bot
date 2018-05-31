package com.rau.bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MessengerService {

    @Value("${messenger.url}")
    private String messengerUrl;

    public void sendTextMessageToMessengerUser(String userId, String text) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(messengerUrl + "/send/text")
                .queryParam("userId", userId)
                .queryParam("text", text);


        HttpEntity<?> entity = new HttpEntity<>(headers);


        restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, Object.class);
    }
}
