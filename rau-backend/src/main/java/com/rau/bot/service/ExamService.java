package com.rau.bot.service;

import com.rau.bot.entity.exams.Exam;
import com.rau.bot.entity.exams.ExamSchedule;
import com.rau.bot.entity.exams.Module;
import com.rau.bot.entity.exams.ModuleSchedule;
import com.rau.bot.entity.schedule.Lecturer;
import com.rau.bot.entity.user.Course;
import com.rau.bot.entity.user.Faculty;
import com.rau.bot.entity.user.Group;
import com.rau.bot.entity.user.User;
import com.rau.bot.repository.exam.ExamRepository;
import com.rau.bot.repository.exam.ExamScheduleRepository;
import com.rau.bot.repository.exam.ModuleRepository;
import com.rau.bot.repository.exam.ModuleScheduleRepository;
import com.rau.bot.repository.user.CourseRepository;
import com.rau.bot.repository.user.FacultyRepository;
import com.rau.bot.repository.user.GroupRepository;
import com.rau.bot.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleScheduleRepository moduleScheduleRepository;
    private final ExamRepository examRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final MessengerService messengerService;
    private final Format formatter;

    private final int examWidth;


    public ExamService(UserRepository userRepository, FacultyRepository facultyRepository, GroupRepository groupRepository, CourseRepository courseRepository,
                       ModuleRepository moduleRepository, ModuleScheduleRepository moduleScheduleRepository, ExamRepository examRepository,
                       ExamScheduleRepository examScheduleRepository, MessengerService messengerService) {
        this.userRepository = userRepository;
        this.facultyRepository = facultyRepository;
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.moduleScheduleRepository = moduleScheduleRepository;
        this.examRepository = examRepository;
        this.examScheduleRepository = examScheduleRepository;
        this.messengerService = messengerService;
        examWidth = 2;
        formatter = new SimpleDateFormat("dd.MM.yyyy");
    }


    public void sendAllExamsToUser(String userId) {
        User user = userRepository.findAll().get(0);
        ExamSchedule examSchedule = getExamScheduleFromUser(user);
        if (examSchedule != null) {
            StringBuilder text = new StringBuilder();
            if (examSchedule.getExams().isEmpty()) {
                messengerService.sendTextMessageToMessengerUser(userId, "Экзамены еще не назначены");
            } else {
                examSchedule.getExams().sort(Comparator.comparing(Exam::getDate));
                text.append("Все твои экзамены:\n\n");
                for (Exam exam : examSchedule.getExams()) {
                    addDateSubjectLectureToText(text, exam.getDate(), exam.getHours(), exam.getSubject().getName(), exam.getLecturers(), exam.getClassRoom().getName());
                }

                messengerService.sendTextMessageToMessengerUser(userId, text.substring(0, text.length() - 24));
            }
        }
    }

    private void addDateSubjectLectureToText(StringBuilder text, Date date, String hours, String subjectName, List<Lecturer> lecturers, String classRoomName) {
        text.append(formatter.format(date))
                .append(" ")
                .append(hours)
                .append("\n")
                .append(subjectName)
                .append("\n");
        for (Lecturer lecturer : lecturers) {
            text.append(lecturer.getName())
                    .append(", ");
        }
        text.append(classRoomName)
                .append("\n")
                .append("______________________\n\n");

    }

    public void sendNextExamToUser(String userId) {
        User user = userRepository.findAll().get(0);
        ExamSchedule examSchedule = getExamScheduleFromUser(user);
        if (examSchedule != null) {
            StringBuilder text = new StringBuilder();
            if (examSchedule.getExams().isEmpty()) {
                messengerService.sendTextMessageToMessengerUser(userId, "Экзамены еще не назначены");
            } else {
                examSchedule.getExams().sort(Comparator.comparing(Exam::getDate));
                Exam exam = examSchedule.getExams().get(0);
                text.append("Your next exam is:\n")
                        .append(formatter.format(exam.getDate()))
                        .append(" ")
                        .append(exam.getHours())
                        .append("\n")
                        .append(exam.getSubject().getName())
                        .append("\n");
                appendLecturersToText(text, exam.getLecturers());

                text.append(exam.getClassRoom().getName());
                messengerService.sendTextMessageToMessengerUser(userId, text.toString());
            }
        }
    }

    private void appendLecturersToText(StringBuilder text, List<Lecturer> lecturers) {
        for (Lecturer lecturer : lecturers) {
            text.append(lecturer.getName())
                    .append(", ");
        }
    }

    public void sendAllModulesToUser(String userId) {
        User user = userRepository.findAll().get(0);
        ModuleSchedule moduleSchedule = getModuleScheduleFromUser(user);
        if (moduleSchedule != null) {
            StringBuilder text = new StringBuilder();
            if (moduleSchedule.getModules().isEmpty()) {
                messengerService.sendTextMessageToMessengerUser(userId, "Модули еще не назначены");
            } else {
                moduleSchedule.getModules().sort(Comparator.comparing(Module::getDate));
                text.append("Все твои модули :\n\n");
                for (Module module : moduleSchedule.getModules()) {
                    addDateSubjectLectureToText(text, module.getDate(), module.getHours(),
                            module.getSubject().getName(), module.getLecturers(), module.getClassRoom().getName());
                }
                messengerService.sendTextMessageToMessengerUser(userId, text.substring(0, text.length() - 24));
            }
        }
    }

    public void sendNextModuleToUser(String userId) {
        User user = userRepository.findAll().get(0);
        ModuleSchedule moduleSchedule = getModuleScheduleFromUser(user);
        if (moduleSchedule != null) {
            StringBuilder text = new StringBuilder();
            if (moduleSchedule.getModules().isEmpty()) {
                messengerService.sendTextMessageToMessengerUser(userId, "Модули еще не назначены");
            } else {
                moduleSchedule.getModules().sort(Comparator.comparing(Module::getDate));
                Module module = moduleSchedule.getModules().get(0);
                text.append("Твой следуюший модуль :\n")
                        .append(formatter.format(module.getDate()))
                        .append(" ")
                        .append(module.getHours())
                        .append("\n")
                        .append(module.getSubject().getName())
                        .append("\n");
                appendLecturersToText(text, module.getLecturers());
                text.append(module.getClassRoom().getName());
                messengerService.sendTextMessageToMessengerUser(userId, text.toString());
            }
        }
    }

    private ExamSchedule getExamScheduleFromUser(User user) {
        Boolean armenianSector = user.getArmenianSector();
        Boolean fromFirstPart = user.getFromFirstPart();
        Faculty faculty = user.getFaculty();
        Group group = user.getGroup();
        Course course = user.getCourse();

        List<ExamSchedule> examSchedules = examScheduleRepository.findAllByArmenianSectorEqualsAndFromFirstPartEquals(armenianSector, fromFirstPart);
        System.out.println("Exam schedules" + examSchedules.toString());
        List<ExamSchedule> examScheduleList = examSchedules.stream().filter(schedule -> schedule.getCourse().equals(course) && schedule.getFaculty().equals(faculty)
                && schedule.getGroup().equals(group))
                .collect(Collectors.toList());
        if (examScheduleList.isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Экзамены еще не назначены");
            return null;
        } else {
            examScheduleList.get(0).getExams().forEach(exam -> {
                if (exam.getDate().compareTo(new Date()) < 0) {
                    examScheduleList.get(0).getExams().remove(exam);
                }
            });
            examScheduleRepository.save(examScheduleList.get(0));
            return examScheduleRepository.findById(examScheduleList.get(0).getId(), examWidth).get();
        }
    }

    private ModuleSchedule getModuleScheduleFromUser(User user) {
        Faculty faculty = user.getFaculty();
        Group group = user.getGroup();
        Course course = user.getCourse();
        Boolean armenianSector = user.getArmenianSector();
        Boolean fromFirstPart = user.getFromFirstPart();

        List<ModuleSchedule> schedules = moduleScheduleRepository.findAllByArmenianSectorEqualsAndFromFirstPartEquals(armenianSector, fromFirstPart);
        System.out.println("Module schedules" + schedules);
        List<ModuleSchedule> scheduleList = schedules.stream().filter(schedule -> schedule.getCourse().equals(course) && schedule.getFaculty().equals(faculty)
                && schedule.getGroup().equals(group))
                .collect(Collectors.toList());
        if (scheduleList.isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Модули еще не назначены");
            return null;
        } else {
            List<Module> modules = new ArrayList<>();
            scheduleList.get(0).getModules().forEach(module -> {
                if (module.getDate().compareTo(new Date()) < 0) {
                    modules.add(module);
                }
            });
            scheduleList.get(0).getModules().removeAll(modules);
            moduleScheduleRepository.save(scheduleList.get(0));
            return moduleScheduleRepository.findById(scheduleList.get(0).getId(), examWidth).get();
        }
    }
}
