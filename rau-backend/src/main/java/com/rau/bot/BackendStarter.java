package com.rau.bot;

import com.rau.bot.entity.exams.Exam;
import com.rau.bot.entity.exams.ExamSchedule;
import com.rau.bot.entity.exams.Module;
import com.rau.bot.entity.exams.ModuleSchedule;
import com.rau.bot.entity.schedule.*;
import com.rau.bot.entity.user.*;
import com.rau.bot.repository.exam.ExamRepository;
import com.rau.bot.repository.exam.ExamScheduleRepository;
import com.rau.bot.repository.exam.ModuleRepository;
import com.rau.bot.repository.exam.ModuleScheduleRepository;
import com.rau.bot.repository.schedule.*;
import com.rau.bot.repository.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private final ExamRepository examRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleScheduleRepository moduleScheduleRepository;


    public BackendStarter(UserRepository userRepository, CourseRepository courseRepository,
                          DepartmentRepository departmentRepository, FacultyRepository facultyRepository,
                          ClassRoomRepository classRoomRepository, LecturerRepository lecturerRepository,
                          ScheduleRepository scheduleRepository, SubjectRepository subjectRepository,
                          WeekDayRepository weekDayRepository, GroupRepository groupRepository,
                          LessonTypeRepository lessonTypeRepository, LessonRepository lessonRepository,
                          WeekDayLessonRepository weekDayLessonRepository, HourLessonRepository hourLessonRepository,
                          HourRepository hourRepository, ExamRepository examRepository,
                          ExamScheduleRepository examScheduleRepository, ModuleRepository moduleRepository,
                          ModuleScheduleRepository moduleScheduleRepository) {
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
        this.examRepository = examRepository;
        this.examScheduleRepository = examScheduleRepository;
        this.moduleRepository = moduleRepository;
        this.moduleScheduleRepository = moduleScheduleRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendStarter.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        userRepository.deleteAll();
//        createLessonTypes();
//        createHours();
//        createClassRooms();
//        createDepartments();
//        createFaculties();
//        createGroups();
//        createLecturers();
//        createSubjects();
//
//
//        createMyGroupSchedule();
//        createMyModules();
//        createMyExams();


    }

    private void createSubjects() {
        subjectRepository.deleteAll();
        subjectRepository.save(new Subject("Уравнения математической физики"));
        subjectRepository.save(new Subject("База Данных"));
        subjectRepository.save(new Subject("Теория вер. и мат статистика"));
        subjectRepository.save(new Subject("Языки программирования и методы трансляции"));
        subjectRepository.save(new Subject("Физика"));
        subjectRepository.save(new Subject("Функц. анализ"));
        subjectRepository.save(new Subject("Яз. и мет. прог. (Java)"));
        subjectRepository.save(new Subject("Концепции современного естествознания"));
        subjectRepository.save(new Subject("Численные методы"));
        subjectRepository.save(new Subject("СК МК Комбинаторная интегральная геометрия"));
        subjectRepository.save(new Subject("Паттерны ООП"));
        subjectRepository.save(new Subject("СК МММ Качественная теория дифференциальных уравнений"));
        subjectRepository.save(new Subject("Курсовая работа"));
    }

    private void createLecturers() {
        lecturerRepository.deleteAll();
        lecturerRepository.save(new Lecturer("Карапетян Г.А."));
        lecturerRepository.save(new Lecturer("Арамян Р.Г."));
        lecturerRepository.save(new Lecturer("Манукян М.Г."));
        lecturerRepository.save(new Lecturer("Беджанян А.Р."));
        lecturerRepository.save(new Lecturer("Ваградян В.Г."));
        lecturerRepository.save(new Lecturer("Атаян"));
        lecturerRepository.save(new Lecturer("Маилян С.С."));
        lecturerRepository.save(new Lecturer("Аветисян П.С."));
        lecturerRepository.save(new Lecturer("Мартиросян А."));
        lecturerRepository.save(new Lecturer("Оганезова Г.Г."));
        lecturerRepository.save(new Lecturer("Нигиян С.А."));
        lecturerRepository.save(new Lecturer("Акопян Ю.Р."));
        lecturerRepository.save(new Lecturer("Амбарцумян"));
        lecturerRepository.save(new Lecturer("Тандилян Г."));
        lecturerRepository.save(new Lecturer("Маргарян В.Н."));
        lecturerRepository.save(new Lecturer("Микилян М.А."));


    }

    private void createGroups() {
        groupRepository.deleteAll();
        groupRepository.save(new Group("501"));
        groupRepository.save(new Group("502"));
        groupRepository.save(new Group("503"));
        groupRepository.save(new Group("504"));
    }

    private void createFaculties() {
        facultyRepository.deleteAll();
        facultyRepository.save(new Faculty("ПМИ", departmentRepository.findByName("ИМИ")));
    }

    private void createDepartments() {
        departmentRepository.deleteAll();
        departmentRepository.save(new Department("ИМИ"));
    }

    private void createMyExams() throws ParseException {
        examRepository.deleteAll();
        examScheduleRepository.deleteAll();

        User user = userRepository.findUserByFullNameEquals("John Vahanyan");

        ExamSchedule examSchedule = new ExamSchedule();

        examSchedule.setCourse(user.getCourse());
        examSchedule.setFaculty(user.getFaculty());
        examSchedule.setArmenianSector(user.getArmenianSector());
        examSchedule.setFromFirstPart(user.getFromFirstPart());
        examSchedule.setGroup(user.getGroup());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        List<Exam> examList = new ArrayList<>();

        Exam exam = new Exam(null, Collections.singletonList(lecturerRepository.findByName("Карапетян Г.А.")),
                subjectRepository.findByName("Уравнения математической физики"), classRoomRepository.findByName("305"),
                "09:00", dateFormatter.parse("30.07.2018"));
        examList.add(exam);

        exam = new Exam(null, Collections.singletonList(lecturerRepository.findByName("Манукян М.Г.")),
                subjectRepository.findByName("База Данных"), classRoomRepository.findByName("305"),
                "09:00", dateFormatter.parse("16.07.2018"));
        examList.add(exam);

        exam = new Exam(null, Arrays.asList(lecturerRepository.findByName("Ваградян В.Г."), lecturerRepository.findByName("Беджанян А.Р.")),
                subjectRepository.findByName("Языки программирования и методы трансляции"), classRoomRepository.findByName("305"),
                "09:00", dateFormatter.parse("20.07.2018"));
        examList.add(exam);

        exam = new Exam(null, Collections.singletonList(lecturerRepository.findByName("Арамян Р.Г.")),
                subjectRepository.findByName("Теория вер. и мат статистика"), classRoomRepository.findByName("305"),
                "09:00", dateFormatter.parse("11.07.2018"));
        examList.add(exam);

        examSchedule.setExams(examList);
        examScheduleRepository.save(examSchedule);

    }

    private void createMyModules() throws ParseException {
        moduleScheduleRepository.deleteAll();
        moduleRepository.deleteAll();
        User user = userRepository.findUserByFullNameEquals("John Vahanyan");
        ModuleSchedule moduleSchedule = new ModuleSchedule();
        moduleSchedule.setFaculty(user.getFaculty());
        moduleSchedule.setCourse(user.getCourse());
        moduleSchedule.setArmenianSector(user.getArmenianSector());
        moduleSchedule.setGroup(user.getGroup());
        moduleSchedule.setFromFirstPart(user.getFromFirstPart());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        List<Module> moduleList = new ArrayList<>();

        Module module = new Module(null, Collections.singletonList(lecturerRepository.findByName("Карапетян Г.А.")),
                subjectRepository.findByName("Уравнения математической физики"), classRoomRepository.findByName("301"),
                "12:50", dateFormatter.parse("06.07.2018"));
        moduleList.add(module);


        module = new Module(null, Collections.singletonList(lecturerRepository.findByName("Манукян М.Г.")),
                subjectRepository.findByName("База Данных"), classRoomRepository.findByName("300"),
                "14:35", dateFormatter.parse("01.07.2018"));
        moduleList.add(module);


        module = new Module(null, Collections.singletonList(lecturerRepository.findByName("Арамян Р.Г.")),
                subjectRepository.findByName("Теория вер. и мат статистика"), classRoomRepository.findByName("313"),
                "10:45", dateFormatter.parse("04.07.2018"));
        moduleList.add(module);

        module = new Module(null, Arrays.asList(lecturerRepository.findByName("Ваградян В.Г."), lecturerRepository.findByName("Беджанян А.Р.")),
                subjectRepository.findByName("Языки программирования и методы трансляции"), classRoomRepository.findByName("305"),
                "10:45", dateFormatter.parse("07.07.2018"));
        moduleList.add(module);

        module = new Module(null, Collections.singletonList(lecturerRepository.findByName("Карапетян Г.А.")),
                subjectRepository.findByName("Курсовая работа"), classRoomRepository.findByName("300"),
                "09:00", dateFormatter.parse("08.07.2018"));
        moduleList.add(module);

        moduleSchedule.setModules(moduleList);
        moduleScheduleRepository.save(moduleSchedule);

    }

    private void createMyGroupSchedule() {
        groupRepository.deleteAll();
        scheduleRepository.deleteAll();
        weekDayLessonRepository.deleteAll();
        weekDayRepository.deleteAll();
        hourLessonRepository.deleteAll();


        String courseName = "3";
        Course course = courseRepository.findByName(courseName);
        if (course == null) {
            course = new Course(courseName);
            courseRepository.save(course);
        }
        String departmentName = "ИМИ";
        Department department = departmentRepository.findByName(departmentName);
        if (department == null) {
            department = new Department(departmentName);
            departmentRepository.save(department);
        }
        String facultyName = "ПМИ";
        Faculty faculty = facultyRepository.findByName(facultyName);
        if (faculty == null) {
            faculty = new Faculty(facultyName, department);
            facultyRepository.save(faculty);
        }
        String groupName = "603";
        Group group = groupRepository.findByName(groupName);
        if (group == null) {
            group = new Group(groupName);
            groupRepository.save(group);
        }

        User user = new User();
        user.setCourse(course);
        user.setFaculty(faculty);
        user.setFullName("John Vahanyan");
        user.setUserId("1891946597506656");
        user.setEmail("jo@jo.jo2");
        user.setGroup(group);
        user.setFromFirstPart(true);
        userRepository.save(user);

        course = courseRepository.findByName(course.getName());
        faculty = facultyRepository.findByName(faculty.getName());
        group = groupRepository.findByName(group.getName());

        Schedule schedule = new Schedule();
        schedule.setFaculty(faculty);
        schedule.setCourse(course);
        schedule.setGroup(group);
        schedule.setFromFirstPart(true);
        schedule.setArmenianSector(false);

        List<WeekDayLesson> weekDayLessons = new ArrayList<>();
        List<HourLesson> hourLessons = new ArrayList<>();

        //region Monday
        Lesson lesson = new Lesson(null, lecturerRepository.findByName("Карапетян Г.А."),
                subjectRepository.findByName("Уравнения математической физики"),
                classRoomRepository.findByName("301"), lessonTypeRepository.findByName("Лекция"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("1"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Арамян Р.Г."),
                subjectRepository.findByName("Теория вер. и мат статистика"),
                classRoomRepository.findByName("313"), lessonTypeRepository.findByName("Проработка"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("2"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Атаян"),
                subjectRepository.findByName("Физика"),
                classRoomRepository.findByName("317"), lessonTypeRepository.findByName("Проработка"),
                null, false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("3"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Маилян С.С."),
                subjectRepository.findByName("Физика"),
                classRoomRepository.findByName("321"), lessonTypeRepository.findByName("Лекция"),
                null, false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("4"), lesson));

        weekDayLessons.add(new WeekDayLesson(new WeekDay("Monday", 1), hourLessons));

        //endregion
        hourLessons = new ArrayList<>();

        //region Tuesday
        lesson = new Lesson(null, lecturerRepository.findByName("Арамян Р.Г."),
                subjectRepository.findByName("Теория вер. и мат статистика"),
                classRoomRepository.findByName("313"), lessonTypeRepository.findByName("Лекция"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("1"), lesson));

        lesson = new Lesson(null, lecturerRepository.findByName("Аветисян П.С."),
                subjectRepository.findByName("Функц. анализ"),
                classRoomRepository.findByName("309"), lessonTypeRepository.findByName("Лекция"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("2"), lesson));

        lesson = new Lesson(null, lecturerRepository.findByName("Мартиросян А."),
                subjectRepository.findByName("Яз. и мет. прог. (Java)"),
                classRoomRepository.findByName("200"), lessonTypeRepository.findByName("Проработка"),
                null, false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("3"), lesson));

        weekDayLessons.add(new WeekDayLesson(new WeekDay("Tuesday", 2), hourLessons));
        //endregion
        hourLessons = new ArrayList<>();

        //region Wednesday
        lesson = new Lesson(null, lecturerRepository.findByName("Оганезова Г.Г."),
                subjectRepository.findByName("Концепции современного естествознания"),
                classRoomRepository.findByName("Синий зал"), lessonTypeRepository.findByName("Лекция"),
                new Lesson(null, lecturerRepository.findByName("Оганезова Г.Г."),
                        subjectRepository.findByName("Концепции современного естествознания"),
                        classRoomRepository.findByName("305"), lessonTypeRepository.findByName("Проработка"),
                        null, false, false), false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("1"), lesson));

        lesson = new Lesson(null, lecturerRepository.findByName("Нигиян С.А."),
                subjectRepository.findByName("Языки программирования и методы трансляции"),
                classRoomRepository.findByName("321"), lessonTypeRepository.findByName("Проработка"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("2"), lesson));

        lesson = new Lesson(null, lecturerRepository.findByName("Карапетян Г.А."),
                subjectRepository.findByName("Уравнения математической физики"),
                classRoomRepository.findByName("301"), lessonTypeRepository.findByName("Лекция"),
                null, false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("3"), lesson));

        weekDayLessons.add(new WeekDayLesson(new WeekDay("Wednesday", 3), hourLessons));
        //endregion
        hourLessons = new ArrayList<>();

        //region Thursday
        lesson = new Lesson(null, lecturerRepository.findByName("Акопян Ю.Р."),
                subjectRepository.findByName("Численные методы"),
                classRoomRepository.findByName("321"), lessonTypeRepository.findByName("Лекция"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("1"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Беджанян А.Р."),
                subjectRepository.findByName("Языки программирования и методы трансляции"),
                classRoomRepository.findByName("305"), lessonTypeRepository.findByName("Проработка"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("2"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Амбарцумян"),
                subjectRepository.findByName("СК МК Комбинаторная интегральная геометрия"),
                classRoomRepository.findByName("313"), lessonTypeRepository.findByName("Проработка"),
                null, false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("3"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Тандилян Г."),
                subjectRepository.findByName("Паттерны ООП"),
                classRoomRepository.findByName("200"), lessonTypeRepository.findByName("Проработка"),
                null, false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("4"), lesson));

        weekDayLessons.add(new WeekDayLesson(new WeekDay("Thursday", 4), hourLessons));
        //endregion
        hourLessons = new ArrayList<>();

        //region Friday
        lesson = new Lesson(null, lecturerRepository.findByName("Маргарян В.Н."),
                subjectRepository.findByName("СК МММ Качественная теория дифференциальных уравнений"),
                classRoomRepository.findByName("321"), lessonTypeRepository.findByName("Лекция"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("1"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Манукян М.Г."),
                subjectRepository.findByName("База Данных"),
                classRoomRepository.findByName("313"), lessonTypeRepository.findByName("Проработка"),
                null, true, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("2"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Микилян М.А."),
                subjectRepository.findByName("Уравнения математической физики"),
                classRoomRepository.findByName("317"), lessonTypeRepository.findByName("Проработка"),
                null, false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("3"), lesson));
        lesson = new Lesson(null, lecturerRepository.findByName("Манукян М.Г."),
                subjectRepository.findByName("База Данных"),
                classRoomRepository.findByName("300"), lessonTypeRepository.findByName("Лекция"),
                null, false, true);
        hourLessons.add(new HourLesson(hourRepository.findByName("4"), lesson));

        weekDayLessons.add(new WeekDayLesson(new WeekDay("Friday", 5), hourLessons));
        //endregion

        schedule.setWeekDayLessons(weekDayLessons);
        scheduleRepository.save(schedule);
    }

    private void createHours() {
        hourRepository.deleteAll();
        hourRepository.save(new Hour("1"));
        hourRepository.save(new Hour("2"));
        hourRepository.save(new Hour("3"));
        hourRepository.save(new Hour("4"));
        hourRepository.save(new Hour("5"));
    }

    private void createLessonTypes() {
        lessonTypeRepository.deleteAll();
        lessonTypeRepository.save(new LessonType("Лекция"));
        lessonTypeRepository.save(new LessonType("Проработка"));
    }

    private void createClassRooms() {
        classRoomRepository.deleteAll();
        for (int i = 300; i < 329; i++) {
            classRoomRepository.save(new ClassRoom(i + ""));
        }
        classRoomRepository.save(new ClassRoom("Синий зал"));
        classRoomRepository.save(new ClassRoom("200"));
        classRoomRepository.save(new ClassRoom("Дом Культуры Рау"));
    }
}
