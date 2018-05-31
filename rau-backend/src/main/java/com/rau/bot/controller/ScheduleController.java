package com.rau.bot.controller;

import com.rau.bot.entity.user.User;
import com.rau.bot.service.RauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messenger")
public class ScheduleController {

    private final RauService rauService;


    public ScheduleController(RauService rauService) {
        this.rauService = rauService;
    }

    @PostMapping("/schedule/next")
    public ResponseEntity<?> getScheduleForUserNextLesson(@RequestBody String userId) {
        User user = rauService.getUserByUserId(userId);
        if (user != null) {
            rauService.sendScheduleToUserForNextLesson(user);
        } else {
            rauService.sendUserNotRegistered(userId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/schedule/all/week")
    public ResponseEntity<?> getAllScheduleForUser(@RequestBody String userId) {
        User user = rauService.getUserByUserId(userId);
        if (user != null) {
            rauService.sendAllScheduleToUser(user);
        } else {
            rauService.sendUserNotRegistered(userId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/schedule/today")
    public ResponseEntity<?> getTodayScheduleForUser(@RequestBody String userId) {
        User user = rauService.getUserByUserId(userId);
        if (user != null) {
            rauService.sendTodaysScheduleToUser(user);
        } else {
            rauService.sendUserNotRegistered(userId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/schedule/tomorrow")
    public ResponseEntity<?> getTomorrowScheduleForUser(@RequestBody String userId) {
        User user = rauService.getUserByUserId(userId);
        if (user != null) {
            rauService.sendTomorrowsScheduleToUser(user);
        } else {
            rauService.sendUserNotRegistered(userId);
        }
        return ResponseEntity.ok().build();
    }
}
