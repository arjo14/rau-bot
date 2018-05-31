package com.rau.bot.controller;

import com.rau.bot.service.ExamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping("/exam/next")
    public ResponseEntity<?> sendNextExamToUser(@RequestBody String userId) {
        examService.sendNextExamToUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/exam/all")
    public ResponseEntity<?> sendAllExamsToUser(@RequestBody String userId) {
        examService.sendAllExamsToUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/module/next")
    public ResponseEntity<?> sendNextModuleToUser(@RequestBody String userId) {
        examService.sendNextModuleToUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/module/all")
    public ResponseEntity<?> sendAllModulesToUser(@RequestBody String userId) {
        examService.sendAllModulesToUser(userId);
        return ResponseEntity.ok().build();
    }
}
