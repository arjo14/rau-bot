package com.rau.bot.service;

import com.rau.bot.entity.schedule.*;
import com.rau.bot.entity.user.*;
import com.rau.bot.repository.schedule.*;
import com.rau.bot.repository.user.CourseRepository;
import com.rau.bot.repository.user.DepartmentRepository;
import com.rau.bot.repository.user.FacultyRepository;
import com.rau.bot.repository.user.UserRepository;
import com.rau.bot.util.RauLessonTimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    private final int width;

    @Value("${messenger.url}")
    private String messengerUrl;


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
        width = 4;
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
        Schedule schedule1 = schedules.stream().filter(schedule -> schedule.getCourse().equals(course) && schedule.getFaculty().equals(faculty)
                && schedule.getGroup().equals(group))
                .collect(Collectors.toList()).get(0);
        return scheduleRepository.findById(schedule1.getId(), width).get();
    }


    public String test() {
        System.out.println();
        sendAllScheduleToUser(userRepository.findAll().get(0));
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

        for (WeekDayLesson weekDayLesson : schedule.getWeekDayLessons()) {
            text.append(weekDayLesson.getWeekDay().getName()).append(" : \n");
            for (HourLesson hourLesson : weekDayLesson.getHourLessons()) {
                text.append(hourLesson.getHour().getName()).append(" : ").append(hourLesson.getLesson()).append("\n");
            }
        }
        System.out.println(text);
        sendTextMessageToMessengerUser(user.getUserId(), text.toString());

    }

    public void sendScheduleToUserForNextLesson(User user) {
        Schedule schedule = getScheduleForUser(user);
        StringBuilder endText = new StringBuilder("Your next lesson is ");

        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime newLocalDateTime;
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        List<HourLesson> hourLessons = new ArrayList<>();

        if (DayOfWeek.SUNDAY.equals(localDateTime.getDayOfWeek()) || hour > 18 || hour == 18 && minute > 15) {
            newLocalDateTime = localDateTime.plusDays(1);

            sendScheduleInfoToUser("1", newLocalDateTime, schedule, user.getUserId(), endText);

        } else if (hour < 9) {
            sendScheduleInfoToUser("1", localDateTime, schedule, user.getUserId(), endText);

        } else {
            int currentLessonHour = getLessonHourByHourAndMinute(hour, minute);
            if (currentLessonHour < 0) {
                sendScheduleInfoToUser(String.valueOf(currentLessonHour * (-1)), localDateTime, schedule, user.getUserId(), endText);
            } else if (currentLessonHour == 5) {
                sendScheduleInfoToUser("5", localDateTime, schedule, user.getUserId(), new StringBuilder("Your current lesson is"));
            } else {
                schedule.getWeekDayLessons()
                        .stream()
                        .filter(weekDayLesson -> localDateTime.getDayOfWeek().name().equals(weekDayLesson.getWeekDay().getName().toLowerCase()))
                        .forEach(weekDayLesson -> weekDayLesson.getHourLessons()
                                .stream()
                                .filter(hourLesson -> (String.valueOf(currentLessonHour)).equals(hourLesson.getHour().getName()) ||
                                        (String.valueOf(currentLessonHour + 1)).equals(hourLesson.getHour().getName()))
                                .forEach(hourLessons::add));

                hourLessons.sort(Comparator.comparing(o -> o.getHour().getName()));

                StringBuilder text = new StringBuilder("Current Lesson is ");
                sendTextMessageToMessengerUser(user.getUserId(),
                        text.append(hourLessons.get(0).getHour())
                                .append(" lesson (")
                                .append(RauLessonTimeUtil.getTimeByHourLesson(String.valueOf(currentLessonHour)))
                                .append("): ")
                                .append(hourLessons.get(0).getLesson().toString())
                                .append("\n")
                                .append("Next lesson is ")
                                .append(hourLessons.get(1).getHour())
                                .append(RauLessonTimeUtil.getTimeByHourLesson(String.valueOf(currentLessonHour)))
                                .append("): ")
                                .append(hourLessons.get(1).getLesson().toString())
                                .toString());
            }

        }

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
                .filter(weekDayLesson1 -> weekDayLesson1.getWeekDay().getName().equals(localDateTime.getDayOfWeek().name()))
                .collect(Collectors.toList());

        for (WeekDayLesson weekDayLesson : weekDayLessons) {
            text.append(weekDayLesson.getWeekDay().getName()).append(" : \n");
            for (HourLesson hourLesson : weekDayLesson.getHourLessons()) {
                text.append(hourLesson.getHour().getName()).append(" : ").append(hourLesson.getLesson()).append("\n");
            }
        }
        sendTextMessageToMessengerUser(user.getUserId(), text.toString());
    }

    private void sendTextMessageToMessengerUser(String userId, String text) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(messengerUrl + "/callback/send/text")
                .queryParam("userId", userId)
                .queryParam("text", text);


        HttpEntity<?> entity = new HttpEntity<>(headers);


        restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, Object.class);
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
        sendTextMessageToMessengerUser(userId,
                endText.append(list.get(0).getHour())
                        .append(" lesson (")
                        .append(RauLessonTimeUtil.getTimeByHourLesson(lessonHour))
                        .append("): ")
                        .append(list.get(0).getLesson()).toString());
    }

    private int getLessonHourByHourAndMinute(int hour, int minute) {
        if (hour < 11) {
            if (minute < 35) {
                return 1;
            } else if (minute < 45) {
                return -1;
            } else {
                return 2;
            }
        } else if (hour < 13) {
            if (minute < 20) {
                return 2;
            } else if (minute < 50) {
                return -2;
            } else {
                return 3;
            }
        } else if (hour < 15) {
            if (minute < 25) {
                return 3;
            } else if (minute < 35) {
                return -3;
            } else {
                return 4;
            }
        } else if (hour < 17) {
            if (minute < 10) {
                return 4;
            } else if (minute < 40) {
                return -4;
            } else {
                return 5;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void sendUserNotRegistered(String userId) {
        sendTextMessageToMessengerUser(userId, "Sorry! You are not registered!!\nWhat are you doing here without registration?????");
    }

    public User getUserByUserId(String userId) {
        return userRepository.findByUserIdEquals(userId);
    }
}
