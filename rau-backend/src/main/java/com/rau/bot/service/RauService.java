package com.rau.bot.service;

import com.rau.bot.entity.user.Course;
import com.rau.bot.entity.user.Department;
import com.rau.bot.entity.user.Faculty;
import com.rau.bot.repository.user.CourseRepository;
import com.rau.bot.repository.user.DepartmentRepository;
import com.rau.bot.repository.user.FacultyRepository;
import com.rau.bot.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RauService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;


    public RauService(UserRepository userRepository, DepartmentRepository departmentRepository, CourseRepository courseRepository, FacultyRepository facultyRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
    }

    public Department addDepartment(String name) {
        return departmentRepository.save(new Department(name));
    }

    @Transactional
    public Faculty addFaculty(String name) {
        return facultyRepository.save(new Faculty(name,new Department()));
    }

    public Course addCourse(String name) {
        return courseRepository.save(new Course(name));
    }


}
