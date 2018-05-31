package com.rau.bot.utils;

public class RauLessonTimeUtil {
    public static String getTimeByHourLesson(String hour) {
        switch (hour) {
            case "1":
                return "(9:00-9:45, 9:50-10:35)";
            case "2":
                return "(10:45-11:30, 11:35-12:20)";
            case "3":
                return "(12:50-13:35, 13:40-14:25)";
            case "4":
                return "(14:35-15:20, 15:25-16:10)";
            case "5":
                return "(16:40-17:25, 17:30-18:15)";
        }
        return "";
    }
}
