package com.rau.bot.controller;

import com.rau.bot.entity.user.Faculty;
import com.rau.bot.service.RauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rau")
public class MainController {
    private final RauService rauService;

    public MainController(RauService rauService) {
        this.rauService = rauService;
    }

    @GetMapping("/add/faculty")
    public ResponseEntity<Faculty> addFaculty(@RequestParam("faculty") String faculty) {
        return ResponseEntity.ok(rauService.addFaculty(faculty));
    }


}
