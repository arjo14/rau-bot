package com.rau.bot;

import com.rau.bot.entity.schedule.*;
import com.rau.bot.entity.user.*;
import com.rau.bot.repository.schedule.*;
import com.rau.bot.repository.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

@SpringBootApplication
@PropertySource({"classpath:network.properties"})
public class BackendStarter implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final ClassRoomRepository classRoomRepository;
    private final LecturerRepository lecturerRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectRepository subjectRepository;
    private final WeekDayRepository weekDayRepository;
    private final GroupRepository groupRepository;
    private final LessonTypeRepository lessonTypeRepository;
    private final LessonRepository lessonRepository;
    private final WeekDayLessonRepository weekDayLessonRepository;
    private final HourLessonRepository hourLessonRepository;
    private final HourRepository hourRepository;


    public BackendStarter(UserRepository userRepository, CourseRepository courseRepository, DepartmentRepository departmentRepository, FacultyRepository facultyRepository, ClassRoomRepository classRoomRepository, LecturerRepository lecturerRepository, ScheduleRepository scheduleRepository, SubjectRepository subjectRepository, WeekDayRepository weekDayRepository, GroupRepository groupRepository, LessonTypeRepository lessonTypeRepository, LessonRepository lessonRepository, WeekDayLessonRepository weekDayLessonRepository, HourLessonRepository hourLessonRepository, HourRepository hourRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.classRoomRepository = classRoomRepository;
        this.lecturerRepository = lecturerRepository;
        this.scheduleRepository = scheduleRepository;
        this.subjectRepository = subjectRepository;
        this.weekDayRepository = weekDayRepository;
        this.groupRepository = groupRepository;
        this.lessonTypeRepository = lessonTypeRepository;
        this.lessonRepository = lessonRepository;
        this.weekDayLessonRepository = weekDayLessonRepository;
        this.hourLessonRepository = hourLessonRepository;
        this.hourRepository = hourRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendStarter.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //region drop database
        userRepository.deleteAll();
        courseRepository.deleteAll();
        departmentRepository.deleteAll();
        facultyRepository.deleteAll();
        groupRepository.deleteAll();


        classRoomRepository.deleteAll();
        lecturerRepository.deleteAll();
        scheduleRepository.deleteAll();
        lessonTypeRepository.deleteAll();
        subjectRepository.deleteAll();
        weekDayRepository.deleteAll();
        lessonRepository.deleteAll();
        weekDayRepository.deleteAll();
        weekDayLessonRepository.deleteAll();
        hourLessonRepository.deleteAll();
        hourRepository.deleteAll();
        //endregion

        // creating class rooms
//        createClassRooms();

        //creating lesson types
        createLessonTypes();

        String courseName = "3";
        Course course = courseRepository.findByName(courseName);
        if (course == null) {
            course = new Course(courseName);
        }
        String departmentName = "ИМВТ";
        Department department = departmentRepository.findByName(departmentName);
        if (department == null) {
            department = new Department(departmentName);
        }
        String facultyName = "ПМИ";
        Faculty faculty = facultyRepository.findByName(facultyName);
        if (faculty == null) {
            faculty = new Faculty(facultyName, department);
        }
        String groupName = "503";
        Group group = groupRepository.findByName(groupName);
        if (group == null) {
            group = new Group(groupName);
        }

        User user = new User();
        user.setCourse(course);
        user.setFaculty(faculty);
        user.setFullName("123456789");
        user.setUserId("User 1");
        user.setEmail("jo@jo.jo2");
        user.setGroup(group);
        user.setFromFirstPart(true);
        userRepository.save(user);


        String classRoomName = "300";
        ClassRoom classRoom = classRoomRepository.findByName(classRoomName);
        if (classRoom == null) {
            classRoom = new ClassRoom(classRoomName);
        }

        course = courseRepository.findByName(course.getName());
        faculty = facultyRepository.findByName(faculty.getName());
        group = groupRepository.findByName(group.getName());

        String lecturerName = "Карапетян Г. А.";
        Lecturer lecturer = lecturerRepository.findByName(lecturerName);
        if (lecturer == null) {
            lecturer = new Lecturer(lecturerName);
        }

        String subjectName = "Мат. Физ";
        Subject subject = subjectRepository.findByName(subjectName);
        if (subject == null) {
            subject = new Subject(subjectName);
        }

        Schedule schedule = new Schedule();
        Lesson lesson = new Lesson();
        lesson.setClassRoom(classRoom);
        schedule.setCourse(course);
        lesson.setLecturer(lecturer);
        schedule.setFaculty(faculty);
        schedule.setGroup(group);
        schedule.setFromFirstPart(true);
        schedule.setArmenianSector(false);
        lesson.setSubject(subject);
        schedule.setWeekDayLessons(Arrays.asList(new WeekDayLesson(new WeekDay("Monday"), Arrays.asList(new HourLesson(new Hour("1"), lesson)))));
        lesson.setLessonType(lessonTypeRepository.findByName("Проработка"));
        lesson.setSameAsTheNextWeek(false);
        scheduleRepository.save(schedule);
        lesson.setNextWeekLesson(lesson);
        lessonRepository.save(lesson);
        scheduleRepository.save(schedule);
    }

    private void createLessonTypes() {
        lessonTypeRepository.save(new LessonType("Лекция"));
        lessonTypeRepository.save(new LessonType("Проработка"));
    }

    private void createClassRooms() {
        for (int i = 1; i < 450; i++) {
            classRoomRepository.save(new ClassRoom(i + ""));
        }
        classRoomRepository.save(new ClassRoom("Синий зал"));
        classRoomRepository.save(new ClassRoom("Дом Культуры Рау"));
    }
}
