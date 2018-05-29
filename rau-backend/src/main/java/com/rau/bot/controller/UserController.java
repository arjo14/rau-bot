package com.rau.bot.controller;

import com.rau.bot.entity.schedule.Schedule;
import com.rau.bot.entity.user.User;
import com.rau.bot.service.RauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final RauService rauService;

    public UserController(RauService rauService) {
        this.rauService = rauService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return ResponseEntity.ok(rauService.saveUser(user));
    }

}
