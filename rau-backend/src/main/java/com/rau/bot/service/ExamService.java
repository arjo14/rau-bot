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

        StringBuilder text = new StringBuilder();
        if (examSchedule.getExams().isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(userId, "You don't have any exams yet!");
        } else {
            examSchedule.getExams().sort(Comparator.comparing(Exam::getDate));
            text.append("Here is your all exams:\n\n");
            for (Exam exam : examSchedule.getExams()) {
                text.append(formatter.format(exam.getDate()))
                        .append(" ")
                        .append(exam.getHours())
                        .append("\n")
                        .append(exam.getSubject().getName())
                        .append("\n");
                for (Lecturer lecturer : exam.getLecturers()) {
                    text.append(lecturer.getName())
                            .append(", ");
                }
                text.append(exam.getClassRoom().getName())
                        .append("\n")
                        .append("______________________\n\n");
            }

            messengerService.sendTextMessageToMessengerUser(userId, text.substring(0, text.length() - 24));
        }
    }

    public void sendNextExamToUser(String userId) {
        User user = userRepository.findAll().get(0);
        ExamSchedule examSchedule = getExamScheduleFromUser(user);

        StringBuilder text = new StringBuilder();
        if (examSchedule.getExams().isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(userId, "You don't have exam yet!");
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
            for (Lecturer lecturer : exam.getLecturers()) {
                text.append(lecturer.getName())
                        .append(", ");
            }
            text.append(exam.getClassRoom().getName());
            messengerService.sendTextMessageToMessengerUser(userId, text.toString());
        }

    }

    public void sendAllModulesToUser(String userId) {
        User user = userRepository.findAll().get(0);
        ModuleSchedule moduleSchedule = getModuleScheduleFromUser(user);

        StringBuilder text = new StringBuilder();
        if (moduleSchedule.getModules().isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(userId, "You don't have any modules yet!");
        } else {
            moduleSchedule.getModules().sort(Comparator.comparing(Module::getDate));
            text.append("Here is your all modules:\n\n");
            for (Module module : moduleSchedule.getModules()) {
                text.append(formatter.format(module.getDate()))
                        .append(" ")
                        .append(module.getHours())
                        .append("\n")
                        .append(module.getSubject().getName())
                        .append("\n");
                for (Lecturer lecturer : module.getLecturers()) {
                    text.append(lecturer.getName())
                            .append(", ");
                }
                text.append(module.getClassRoom().getName())
                        .append("\n")
                        .append("______________________\n\n");
            }

            messengerService.sendTextMessageToMessengerUser(userId, text.substring(0, text.length() - 24));
        }

    }

    public void sendNextModuleToUser(String userId) {
        User user = userRepository.findAll().get(0);
        ModuleSchedule moduleSchedule = getModuleScheduleFromUser(user);

        StringBuilder text = new StringBuilder();
        if (moduleSchedule.getModules().isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(userId, "You don't have exam yet!");
        } else {
            moduleSchedule.getModules().sort(Comparator.comparing(Module::getDate));
            Module module = moduleSchedule.getModules().get(0);
            text.append("Your next module is:\n")
                    .append(formatter.format(module.getDate()))
                    .append(" ")
                    .append(module.getHours())
                    .append("\n")
                    .append(module.getSubject().getName())
                    .append("\n");
            for (Lecturer lecturer : module.getLecturers()) {
                text.append(lecturer.getName())
                        .append(", ");
            }
            text.append(module.getClassRoom().getName());
            messengerService.sendTextMessageToMessengerUser(userId, text.toString());
        }
    }

    private ExamSchedule getExamScheduleFromUser(User user) {
        Faculty faculty = user.getFaculty();
        Course course = user.getCourse();
        Group group = user.getGroup();
        Boolean fromFirstPart = user.getFromFirstPart();
        Boolean armenianSector = user.getArmenianSector();

        List<ExamSchedule> schedules = examScheduleRepository.findAllByArmenianSectorEqualsAndFromFirstPartEquals(armenianSector, fromFirstPart);
        List<ExamSchedule> scheduleList = schedules.stream().filter(schedule -> schedule.getCourse().equals(course) && schedule.getFaculty().equals(faculty)
                && schedule.getGroup().equals(group))
                .collect(Collectors.toList());
        if (scheduleList.isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Can't find exam for you");
            throw new IllegalArgumentException("Can't find exams for this user");
        } else {
            scheduleList.get(0).getExams().forEach(exam -> {
                if (exam.getDate().compareTo(new Date()) < 0) {
                    scheduleList.get(0).getExams().remove(exam);
                }
            });
            examScheduleRepository.save(scheduleList.get(0));
            return examScheduleRepository.findById(scheduleList.get(0).getId(), examWidth).get();
        }
    }

    private ModuleSchedule getModuleScheduleFromUser(User user) {
        Faculty faculty = user.getFaculty();
        Course course = user.getCourse();
        Group group = user.getGroup();
        Boolean fromFirstPart = user.getFromFirstPart();
        Boolean armenianSector = user.getArmenianSector();

        List<ModuleSchedule> schedules = moduleScheduleRepository.findAllByArmenianSectorEqualsAndFromFirstPartEquals(armenianSector, fromFirstPart);
        List<ModuleSchedule> scheduleList = schedules.stream().filter(schedule -> schedule.getCourse().equals(course) && schedule.getFaculty().equals(faculty)
                && schedule.getGroup().equals(group))
                .collect(Collectors.toList());
        if (scheduleList.isEmpty()) {
            messengerService.sendTextMessageToMessengerUser(user.getUserId(), "Can't find module for you");
            throw new IllegalArgumentException("Can't find modules for this user");
        } else {
            scheduleList.get(0).getModules().forEach(module -> {
                if (module.getDate().compareTo(new Date()) < 0) {
                    scheduleList.get(0).getModules().remove(module);
                }
            });
            moduleScheduleRepository.save(scheduleList.get(0));
            return moduleScheduleRepository.findById(scheduleList.get(0).getId(), examWidth).get();
        }
    }
}
