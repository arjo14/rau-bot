package com.rau.bot.service;

import com.rau.bot.dto.QuickReplyDto;
import com.rau.bot.dto.QuickReplyResponseDto;
import com.rau.bot.entity.schedule.*;
import com.rau.bot.entity.user.*;
import com.rau.bot.repository.exam.ExamScheduleRepository;
import com.rau.bot.repository.exam.ModuleScheduleRepository;
import com.rau.bot.repository.schedule.*;
import com.rau.bot.repository.user.*;
import com.rau.bot.utils.RauLessonTimeUtil;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ModuleScheduleRepository moduleScheduleRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final GroupRepository groupRepository;

    private final MessengerService messengerService;

    private final int scheduleWidth;


    public RauService(UserRepository userRepository, DepartmentRepository departmentRepository, CourseRepository courseRepository,
                      FacultyRepository facultyRepository, LecturerRepository lecturerRepository, SubjectRepository subjectRepository,
                      ClassRoomRepository classRoomRepository, WeekDayRepository weekDayRepository, LessonTypeRepository lessonTypeRepository,
                      ScheduleRepository scheduleRepository, LessonRepository lessonRepository, ModuleScheduleRepository moduleScheduleRepository,
                      ExamScheduleRepository examScheduleRepository, GroupRepository groupRepository, MessengerService messengerService) {
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
        this.moduleScheduleRepository = moduleScheduleRepository;
        this.examScheduleRepository = examScheduleRepository;
        this.groupRepository = groupRepository;
        this.messengerService = messengerService;
        scheduleWidth = 4;
    }

    //region nerveris azdox methodner
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

    // endregion

    public Schedule getScheduleForUser(User user) {
        Faculty faculty = user.getFaculty();
        Course course = user.getCourse();
        Group group = user.getGroup();
        Boolean fromFirstPart = user.getFromFirstPart();
        Boolean armenianSector = user.getArmenianSector();

        List<Schedule> schedules = scheduleRepository.findAllByArmenianSectorEqualsAndFromFirstPartEquals(armenianSector, fromFirstPart);
        List<Schedule> scheduleList = schedules.stream().filter(schedule -> schedule.getCourse().equals(course) && schedule.getFaculty().equals(faculty)
                && schedule.getGroup().equals(group))
                .collect(Collectors.toList());
        if (scheduleList.isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Can't find Schedule for you");
            throw new IllegalArgumentException("Can't find Schedule for this user");
        } else {
            return scheduleRepository.findById(scheduleList.get(0).getId(), scheduleWidth).get();
        }
    }


    public String test() {
        System.out.println();
        return "test";
    }

    public User saveUser(User newUser) {
        User user = userRepository.findByUserIdEquals(newUser.getUserId());
        if (user != null) {
            newUser.setId(user.getId());
        }
        return userRepository.save(newUser);
    }

    public void sendAllScheduleToUser(User user) {
        Schedule schedule = getScheduleForUser(user);

        StringBuilder text = new StringBuilder();

        schedule.getWeekDayLessons().sort(Comparator.comparing(o2 -> o2.getWeekDay().getNumber()));
        for (WeekDayLesson weekDayLesson : schedule.getWeekDayLessons()) {
            text.append(weekDayLesson.getWeekDay().getName()).append(" : \n");
            weekDayLesson.getHourLessons().sort(Comparator.comparing(o -> o.getHour().getName()));
            for (HourLesson hourLesson : weekDayLesson.getHourLessons()) {
                text.append(hourLesson.getHour()).append(RauLessonTimeUtil.getTimeByHourLesson(hourLesson.getHour().getName())).append("\n").append(hourLesson.getLesson()).append("\n\n");
            }
            text.append("______________________\n\n");
        }
        System.out.println(text);
        messengerService.sendTextMessageToMessengerUser(user.getUserId(), text.substring(0, text.length() - 24));

    }

    public void sendScheduleToUserForNextLesson(User user) {
        Schedule schedule = getScheduleForUser(user);
        StringBuilder endText = new StringBuilder("Твой следуюший урок ");

        LocalDateTime localDateTime = LocalDateTime.now();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        List<HourLesson> hourLessons = new ArrayList<>();

        if (DayOfWeek.SUNDAY.equals(localDateTime.getDayOfWeek()) || hour > 18 || hour == 18 && minute > 15) {

            WeekDayLesson wd = getNextLesson(schedule, localDateTime);
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Твой следуюший урок :\n"
                    + wd.getWeekDay().getName()
                    + ":\n "
                    + wd.getHourLessons().get(0).getHour()
                    + wd.getHourLessons().get(0).getLesson());

        } else if (hour < 9 || hour == 9 && minute < 30) {

            WeekDayLesson wd = getNextLesson(schedule, localDateTime.minusDays(1));
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Твой следуюший урок :\n"
                    + wd.getWeekDay().getName()
                    + "\n"
                    + wd.getHourLessons().get(0).getHour()
                    + RauLessonTimeUtil.getTimeByHourLesson(String.valueOf(1))
                    + "\n"
                    + wd.getHourLessons().get(0).getLesson());

        } else {
            int currentLessonHour = getLessonHourByHourAndMinute(hour, minute);
            if (currentLessonHour == 5) {
                boolean[] has5thHourLesson = {false};
                schedule.getWeekDayLessons().forEach(wdl -> {
                    if (wdl.getWeekDay().getName().equals(localDateTime.getDayOfWeek().name())) {
                        wdl.getHourLessons().forEach(hourLesson -> {
                            if (hourLesson.getHour().getName().equals("5")) {
                                messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Your current lesson is :\n"
                                        + localDateTime.getDayOfWeek().name() + ", " + hourLesson);
                                has5thHourLesson[0] = true;
                            }
                        });
                    }
                });
                if (!has5thHourLesson[0]) {
                    WeekDayLesson weekDayLesson = getNextLesson(schedule, localDateTime);
                    messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Твой следуюший урок :\n"
                            + weekDayLesson.getWeekDay().getName()
                            + ", "
                            + weekDayLesson.getHourLessons().get(0));
                }
            } else {
                schedule.getWeekDayLessons()
                        .stream()
                        .filter(weekDayLesson -> localDateTime.getDayOfWeek().name().toLowerCase().equals(weekDayLesson.getWeekDay().getName().toLowerCase()))
                        .forEach(weekDayLesson -> weekDayLesson.getHourLessons()
                                .stream()
                                .filter(hourLesson -> (String.valueOf(currentLessonHour)).equals(hourLesson.getHour().getName()) ||
                                        (String.valueOf(currentLessonHour + 1)).equals(hourLesson.getHour().getName()))
                                .forEach(hourLessons::add));

                hourLessons.sort(Comparator.comparing(o -> o.getHour().getName()));

                StringBuilder text = new StringBuilder("Your Current Lesson is \n");
                if (hourLessons.size() > 1) {
                    messengerService.sendTextMessageToMessengerUser(user.getUserId(),
                            text.append(hourLessons.get(0).getHour())
                                    .append(" ")
                                    .append(RauLessonTimeUtil.getTimeByHourLesson(String.valueOf(currentLessonHour)))
                                    .append("\n")
                                    .append(hourLessons.get(0).getLesson())
                                    .append("\n\n")
                                    .append("Next lesson is \n")
                                    .append(hourLessons.get(1).getHour())
                                    .append(RauLessonTimeUtil.getTimeByHourLesson(String.valueOf(currentLessonHour)))
                                    .append("\n")
                                    .append(hourLessons.get(1).getLesson())
                                    .toString());
                } else if (hourLessons.size() == 1) {
                    messengerService.sendTextMessageToMessengerUser(user.getUserId(),
                            text.append(hourLessons.get(0).getHour())
                                    .append(" ")
                                    .append(RauLessonTimeUtil.getTimeByHourLesson(String.valueOf(currentLessonHour)))
                                    .append("\n")
                                    .append(hourLessons.get(0).getLesson()).toString());
                } else {
                    WeekDayLesson nextLesson = getNextLesson(schedule, localDateTime);

                    messengerService.sendTextMessageToMessengerUser(user.getUserId(),
                            "You don't have a lesson now. Твой следуюший урок :\n" +
                                    nextLesson.getWeekDay().getName() + ", " +
                                    nextLesson.getHourLessons().get(0));
                }
            }

        }

    }

    private WeekDayLesson getNextLesson(Schedule schedule, LocalDateTime localDateTime) {
        WeekDayLesson nextLesson = new WeekDayLesson();
        for (int i = 0; i < 7; i++) {
            localDateTime = localDateTime.plusDays(1);
            LocalDateTime finalNewLocalDateTime = localDateTime;
            List<WeekDayLesson> collect = schedule.getWeekDayLessons()
                    .stream()
                    .filter(weekDayLesson -> finalNewLocalDateTime.getDayOfWeek().name().toLowerCase().equals(weekDayLesson.getWeekDay().getName().toLowerCase())).collect(Collectors.toList());

            if (!collect.isEmpty() && !collect.get(0).getHourLessons().isEmpty()) {
                collect.get(0).getHourLessons().sort(Comparator.comparing(o -> o.getHour().getName()));
                nextLesson.setWeekDay(new WeekDay(collect.get(0).getWeekDay().getName(), getWeekDayNumberByName(collect.get(0).getWeekDay().getName())));
                nextLesson.setHourLessons(collect.get(0).getHourLessons());
                return nextLesson;
            }
        }
        throw new IllegalArgumentException();
    }

    private int getWeekDayNumberByName(String name) {
        switch (name.toLowerCase()) {
            case "monday":
                return 1;
            case "tuesday":
                return 2;
            case "wednesday":
                return 3;
            case "thursday":
                return 4;
            case "friday":
                return 5;
            default:
                return 6;
        }
    }

    private boolean isEvenWeek(LocalDateTime localDateTime) {
        return localDateTime.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) % 2 == 0;
    }

    public void sendTodaysScheduleToUser(User user) {

        LocalDateTime localDateTime = LocalDateTime.now();

        sendOneDayScheduleToUser(user, localDateTime);
    }

    public void sendTomorrowsScheduleToUser(User user) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);

        sendOneDayScheduleToUser(user, localDateTime);
    }

    private void sendOneDayScheduleToUser(User user, LocalDateTime localDateTime) {
        StringBuilder text = new StringBuilder();
        Schedule schedule = getScheduleForUser(user);
        List<WeekDayLesson> weekDayLessons = schedule.getWeekDayLessons()
                .stream()
                .filter(weekDayLesson1 -> weekDayLesson1.getWeekDay().getName().toLowerCase().equals(localDateTime.getDayOfWeek().name().toLowerCase()))
                .collect(Collectors.toList());

        if (weekDayLessons.isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), "It's so good man, you don't have a lesson today !");
        } else {
            weekDayLessons.sort(Comparator.comparing(o2 -> o2.getWeekDay().getNumber()));
            for (WeekDayLesson weekDayLesson : weekDayLessons) {
                text.append(weekDayLesson.getWeekDay().getName()).append(" : \n");
                weekDayLesson.getHourLessons().sort(Comparator.comparing(o -> o.getHour().getName()));
                for (HourLesson hourLesson : weekDayLesson.getHourLessons()) {
                    text.append(hourLesson.getHour().getName())
                            .append(") ")
                            .append(RauLessonTimeUtil.getTimeByHourLesson(hourLesson.getHour().getName()))
                            .append(" : \n")
                            .append(hourLesson.getLesson())
                            .append("\n")
                            .append("\n");
                }
            }
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), text.toString());
        }
    }


    private void sendScheduleInfoToUser(String lessonHour, LocalDateTime localDateTime, Schedule schedule, String userId, StringBuilder endText) {
        List<HourLesson> list = new ArrayList<>();
        schedule.getWeekDayLessons()
                .stream()
                .filter(weekDayLesson -> localDateTime.getDayOfWeek().name().equals(weekDayLesson.getWeekDay().getName().toLowerCase()))
                .forEach(weekDayLesson -> weekDayLesson.getHourLessons()
                        .stream()
                        .filter(hourLesson -> String.valueOf(hourLesson.getHour().getName()).equals(hourLesson))
                        .forEach(list::add));
        messengerService.sendTextMessageToMessengerUser(userId,
                endText.append(list.get(0).getHour())
                        .append(" lesson (")
                        .append(RauLessonTimeUtil.getTimeByHourLesson(lessonHour))
                        .append("): ")
                        .append(list.get(0).getLesson()).toString());
    }

    private int getLessonHourByHourAndMinute(int hour, int minute) {
        if (hour < 11) {
            if (minute < 35 || hour == 9) {
                return 1;
            } else {
                return 2;
            }
        } else if (hour < 13) {
            if (minute < 20 || hour == 11) {
                return 2;
            } else {
                return 3;
            }
        } else if (hour < 15) {
            if (minute < 25 || hour == 13) {
                return 3;
            } else {
                return 4;
            }
        } else if (hour < 17) {
            if (minute < 10 || hour == 15) {
                return 4;
            } else {
                return 5;
            }
        } else if (hour < 19) {
            if (minute < 15 || hour == 17) {
                return 5;
            }
        }
        throw new IllegalArgumentException();

    }

    public void sendUserNotRegistered(String userId) {
        messengerService.sendTextMessageToMessengerUser(userId, "Sorry! You are not registered!!\nWhat are you doing here without registration?????");
    }

    public User getUserByUserId(String userId) {
        User user = userRepository.findAll().get(0);
        user.setUserId(userId);
        userRepository.save(user);
        return user;
    }

    public QuickReplyResponseDto getAllDepartments(Boolean fromArmenianSector) {
        List<Department> departments = new ArrayList<>();
        scheduleRepository.findAll(scheduleWidth)
                .stream()
                .filter(schedule -> schedule.getArmenianSector().equals(fromArmenianSector))
                .forEach(schedule -> {
                    if (!departments.contains(schedule.getFaculty().getDepartment())) {
                        departments.add(schedule.getFaculty().getDepartment());
                    }
                });
        List<QuickReplyDto> quickReplyDtos = new LinkedList<>();
        QuickReplyResponseDto quickReplyResponseDto = new QuickReplyResponseDto("Choose your department.", quickReplyDtos);
        departments.forEach(department -> quickReplyDtos.add(new QuickReplyDto(department.getName(), department.getId().toString())));
        return quickReplyResponseDto;
    }

    public QuickReplyResponseDto getFacultiesByDepartmentId(Boolean fromArmenianSector, String departmentId) {
        List<Faculty> faculties = new ArrayList<>();
        scheduleRepository.findAll(scheduleWidth)
                .stream()
                .filter(schedule -> schedule.getArmenianSector().equals(fromArmenianSector)
                        && schedule.getFaculty().getDepartment().getId().toString().equals(departmentId))
                .forEach(schedule -> {
                            if (!faculties.contains(schedule.getFaculty())) {
                                faculties.add(schedule.getFaculty());
                            }
                        }
                );
        List<QuickReplyDto> quickReplyDtos = new LinkedList<>();
        QuickReplyResponseDto quickReplyResponseDto = new QuickReplyResponseDto("Choose your Faculty.");
        quickReplyResponseDto.setQuickReplyDtoList(quickReplyDtos);
        faculties.forEach(faculty -> quickReplyDtos.add(new QuickReplyDto(faculty.getName(), faculty.getId().toString())));
        return quickReplyResponseDto;
    }

    public QuickReplyResponseDto getCoursesByFacultyId(Boolean fromArmenianSector, String facultyId) {
        List<Course> courses = new ArrayList<>();
        scheduleRepository.findAll(scheduleWidth)
                .stream()
                .filter((schedule -> schedule.getFaculty().getId().toString().equals(facultyId)
                        && schedule.getArmenianSector().equals(fromArmenianSector)))
                .forEach(schedule -> {
                    if (!courses.contains(schedule.getCourse())) {
                        courses.add(schedule.getCourse());
                    }
                });
        List<QuickReplyDto> quickReplyDtos = new LinkedList<>();
        QuickReplyResponseDto quickReplyResponseDto = new QuickReplyResponseDto("Choose your Course.");
        quickReplyResponseDto.setQuickReplyDtoList(quickReplyDtos);

        courses.forEach(course -> quickReplyDtos.add(new QuickReplyDto(course.getName(), course.getId().toString())));
        return quickReplyResponseDto;
    }

    public QuickReplyResponseDto getGroupsByFacultyIdAndCourseId(Boolean fromArmenianSector, String facultyId, String courseId) {
        List<Group> groups = new ArrayList<>();
        scheduleRepository.findAll(scheduleWidth)
                .stream()
                .filter((schedule -> schedule.getFaculty().getId().toString().equals(facultyId)
                        && schedule.getCourse().getId().toString().equals(courseId)
                        && schedule.getArmenianSector().equals(fromArmenianSector)))
                .forEach(schedule -> {
                    if (!groups.contains(schedule.getGroup())) {
                        groups.add(schedule.getGroup());
                    }
                });
        List<QuickReplyDto> quickReplyDtos = new LinkedList<>();
        QuickReplyResponseDto quickReplyResponseDto = new QuickReplyResponseDto("Choose your Group.");
        quickReplyResponseDto.setQuickReplyDtoList(quickReplyDtos);

        groups.forEach(group -> quickReplyDtos.add(new QuickReplyDto(group.getName(), group.getId().toString())));
        return quickReplyResponseDto;
    }

    public QuickReplyResponseDto checkIfGroupHasPartitions(Boolean fromArmenianSector, String facultyId, String courseId, String groupId) {
        List<Schedule> list = scheduleRepository.findAll(scheduleWidth)
                .stream()
                .filter((schedule -> schedule.getFaculty().getId().toString().equals(facultyId)
                        && schedule.getCourse().getId().toString().equals(courseId)
                        && schedule.getGroup().getId().toString().equals(groupId)
                        && schedule.getArmenianSector().equals(fromArmenianSector)))
                .collect(Collectors.toList());
        if (list.size() > 1) {
            return new QuickReplyResponseDto("Choose your group part.",
                    Arrays.asList(new QuickReplyDto("1", "1"),
                            new QuickReplyDto("2", "2")));
        } else {
            return null;
        }
    }

    public Lesson getLessonById(String lessonId) {
        Optional<Lesson> lessonOpt = lessonRepository.findById(Long.valueOf(lessonId));
        return lessonOpt.orElse(null);
    }

    public Faculty getFacultyById(String facultyId) {
        Optional<Faculty> facultyOptional = facultyRepository.findById(Long.valueOf(facultyId), 2);
        return facultyOptional.orElse(null);
    }

    public Course getCourseById(String courseId) {
        Optional<Course> courseOptional = courseRepository.findById(Long.valueOf(courseId));
        return courseOptional.orElse(null);
    }

    public Group getGroupById(String groupId) {
        Optional<Group> groupOptional = groupRepository.findById(Long.valueOf(groupId));
        return groupOptional.orElse(null);
    }

    public Department getDepartmentById(String departmentId) {
        Optional<Department> departmentOptional = departmentRepository.findById(Long.valueOf(departmentId));
        return departmentOptional.orElse(null);
    }
}
