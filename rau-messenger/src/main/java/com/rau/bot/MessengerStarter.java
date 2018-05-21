package com.rau.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:messenger.properties", "classpath:network.properties"})
public class MessengerStarter {
    public static void main(String[] args) {
        SpringApplication.run(MessengerStarter.class, args);
    }
}
