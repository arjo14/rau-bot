package com.rau.bot.service;

import com.rau.bot.entity.schedule.*;
import com.rau.bot.entity.user.Course;
import com.rau.bot.entity.user.Department;
import com.rau.bot.entity.user.Faculty;
import com.rau.bot.repository.schedule.*;
import com.rau.bot.repository.user.CourseRepository;
import com.rau.bot.repository.user.DepartmentRepository;
import com.rau.bot.repository.user.FacultyRepository;
import com.rau.bot.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RauService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final LecturerRepository lecturerRepository;
    private final SubjectRepository subjectRepository;
    private final ClassRoomRepository classRoomRepository;
    private final WeekDayRepository weekDayRepository;
    private final LessonTypeRepository lessonTypeRepository;
    private final ScheduleRepository scheduleRepository;
    private final LessonRepository lessonRepository;


    public RauService(UserRepository userRepository, DepartmentRepository departmentRepository, CourseRepository courseRepository,
                      FacultyRepository facultyRepository, LecturerRepository lecturerRepository, SubjectRepository subjectRepository,
                      ClassRoomRepository classRoomRepository, WeekDayRepository weekDayRepository, LessonTypeRepository lessonTypeRepository,
                      ScheduleRepository scheduleRepository, LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.lecturerRepository = lecturerRepository;
        this.subjectRepository = subjectRepository;
        this.classRoomRepository = classRoomRepository;
        this.weekDayRepository = weekDayRepository;
        this.lessonTypeRepository = lessonTypeRepository;
        this.scheduleRepository = scheduleRepository;
        this.lessonRepository = lessonRepository;
    }

    public Department addDepartment(String name) {
        return departmentRepository.save(new Department(name));
    }

    public Faculty addFaculty(String facultyName, String departmentName) {
        return facultyRepository.save(new Faculty(facultyName, departmentRepository.findByName(departmentName)));
    }

    public Course addCourse(String name) {
        return courseRepository.save(new Course(name));
    }

    public Lecturer addLecturer(String name) {
        return lecturerRepository.save(new Lecturer(name));
    }


    public Subject addSubject(String subject) {
        return subjectRepository.save(new Subject(subject));
    }

    public ClassRoom addClassRoom(String classRoom) {
        return classRoomRepository.save(new ClassRoom(classRoom));
    }

    public List<ClassRoom> getAllClassRooms() {
        return classRoomRepository.findAll();
    }

    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    public List<WeekDay> getAllWeekDays() {
        return weekDayRepository.findAll();
    }

    public List<LessonType> getAllLessonTypes() {
        return lessonTypeRepository.findAll();
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<Schedule> getAllLessons() {
        return scheduleRepository.findAll();
    }

    public String test() {
        System.out.println();
        return "test";
    }
}
