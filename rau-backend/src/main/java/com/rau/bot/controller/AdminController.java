package com.rau.bot.controller;

import com.rau.bot.entity.schedule.ClassRoom;
import com.rau.bot.entity.schedule.Subject;
import com.rau.bot.entity.user.Department;
import com.rau.bot.entity.user.Faculty;
import com.rau.bot.service.RauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final RauService rauService;

    public AdminController(RauService rauService) {
        this.rauService = rauService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok(rauService.test());
    }

    @PostMapping("/add/faculty/{faculty}/{department}")
    public ResponseEntity<Faculty> addFaculty(@PathVariable("faculty") String faculty,
                                              @PathVariable("department") String department) {
        return ResponseEntity.ok(rauService.addFaculty(faculty, department));
    }

    @PostMapping("/add/department/{department}")
    public ResponseEntity<Department> addDepartment(@PathVariable("department") String department) {
        return ResponseEntity.ok(rauService.addDepartment(department));
    }

    @PostMapping("/add/subject/{subject}")
    public ResponseEntity<Subject> addSubject(@PathVariable("subject") String subject) {
        return ResponseEntity.ok(rauService.addSubject(subject));
    }

    @PostMapping("/add/classroom/{classRoom}")
    public ResponseEntity<ClassRoom> addClassRoom(@PathVariable("classRoom") String classRoom) {
        return ResponseEntity.ok(rauService.addClassRoom(classRoom));
    }

    @GetMapping("/get/classRooms")
    public ResponseEntity<?> getClassRooms() {
        return ResponseEntity.ok(rauService.getAllClassRooms());
    }

    @GetMapping("/get/lecturers")
    public ResponseEntity<?> getLecturers() {
        return ResponseEntity.ok(rauService.getAllLecturers());
    }

    @GetMapping("/get/lessonTypes")
    public ResponseEntity<?> getLessonTypes() {
        return ResponseEntity.ok(rauService.getAllLessonTypes());
    }

    @GetMapping("/get/subjects")
    public ResponseEntity<?> getSubjects() {
        return ResponseEntity.ok(rauService.getAllSubjects());
    }

    @GetMapping("/get/weekdays")
    public ResponseEntity<?> getWeekDays() {
        return ResponseEntity.ok(rauService.getAllWeekDays());
    }

    @GetMapping("/get/lessons")
    public ResponseEntity<?> getLessons() {
        return ResponseEntity.ok(rauService.getAllLessons());
    }

    @GetMapping("/get/department/{departmentId}")
    public ResponseEntity<?> getDepartmentById(@PathVariable("departmentId")String departmentId) {
        return ResponseEntity.ok(rauService.getDepartmentById(departmentId));
    }

    @GetMapping("/get/faculty/{facultyId}")
    public ResponseEntity<?> getFacultyById(@PathVariable("facultyId")String facultyId) {
        return ResponseEntity.ok(rauService.getFacultyById(facultyId));
    }

    @GetMapping("/get/lesson/{lessonId}")
    public ResponseEntity<?> getLessonById(@PathVariable("lessonId")String lessonId) {
        return ResponseEntity.ok(rauService.getLessonById(lessonId));
    }

    @GetMapping("/get/course/{courseId}")
    public ResponseEntity<?> getCourseById(@PathVariable("courseId")String courseId) {
        return ResponseEntity.ok(rauService.getCourseById(courseId));
    }

    @GetMapping("/get/group/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable("groupId")String groupId) {
        return ResponseEntity.ok(rauService.getGroupById(groupId));
    }

}
