package com.rau.bot.controller;

import com.rau.bot.dto.QuickReplyResponseDto;
import com.rau.bot.entity.user.*;
import com.rau.bot.service.RauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user")
    public ResponseEntity<QuickReplyResponseDto> getFaculties(@RequestParam("fromArmenianSector") Boolean fromArmenianSector) {
        return ResponseEntity.ok(rauService.getAllDepartments(fromArmenianSector));
    }

    //TODO change to QuickReplyResponseDto

    @GetMapping("/faculty/{departmentId}")
    public ResponseEntity<QuickReplyResponseDto> getFacultiesByDepartmentId(@RequestParam("fromArmenianSector") Boolean fromArmenianSector,
                                                                    @PathVariable("departmentId") String departmentId) {
        return ResponseEntity.ok(rauService.getFacultiesByDepartmentId(fromArmenianSector, departmentId));
    }

    @GetMapping("/course/{facultyId}")
    public ResponseEntity<QuickReplyResponseDto> getCoursesForThisFaculty(@RequestParam("fromArmenianSector") Boolean fromArmenianSector,
                                                                 @PathVariable("facultyId") String facultyId) {
        return ResponseEntity.ok(rauService.getCoursesByFacultyId(fromArmenianSector, facultyId));
    }

    @GetMapping("/group/{facultyId}/{courseId}")
    public ResponseEntity<QuickReplyResponseDto> getGroupsForThisFacultyAndCourse(@RequestParam("fromArmenianSector") Boolean fromArmenianSector,
                                                                        @PathVariable("facultyId") String facultyId,
                                                                        @PathVariable("courseId") String courseId) {
        return ResponseEntity.ok(rauService.getGroupsByFacultyIdAndCourseId(fromArmenianSector, facultyId, courseId));
    }

    @GetMapping("/group/hasPartitions/{facultyId}/{courseId}/{groupId}")
    public ResponseEntity<QuickReplyResponseDto> checkIfGroupHasPartitions(@RequestParam("fromArmenianSector") Boolean fromArmenianSector,
                                                             @PathVariable("facultyId") String facultyId,
                                                             @PathVariable("courseId") String courseId,
                                                             @PathVariable("groupId") String groupId) {
        return ResponseEntity.ok(rauService.checkIfGroupHasPartitions(fromArmenianSector, facultyId, courseId, groupId));
    }
}
