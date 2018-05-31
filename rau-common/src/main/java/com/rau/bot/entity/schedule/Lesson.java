package com.rau.bot.entity.schedule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Lesson {

    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "HAS_LECTURER")
    private Lecturer lecturer;

    @Relationship(type = "HAS_SUBJECT")
    private Subject subject;

    @Relationship(type = "IS_IN")
    private ClassRoom classRoom;

    @Relationship(type = "HAS_TYPE")
    private LessonType lessonType;

    @Relationship(type = "HAS_NEXT_WEEK_LESSON")
    private Lesson nextWeekLesson;

    private Boolean sameAsTheNextWeek;
    private Boolean mainLesson;

    @Override
    public String toString() {
        if (sameAsTheNextWeek != null && sameAsTheNextWeek.equals(true) && mainLesson != null && mainLesson.equals(true) && LocalDateTime.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) % 2 == 0 && nextWeekLesson != null) {
            return nextWeekLesson.subject.getName()
                    + "\n"
                    + nextWeekLesson.lessonType.getName()
                    + "\n"
                    + nextWeekLesson.lecturer.getName()
                    + "\n"
                    + nextWeekLesson.classRoom.getName()
                    + " ";
        } else {
            return subject.getName()
                    + "\n"
                    + lessonType.getName()
                    + "\n"
                    + lecturer.getName()
                    + "\n"
                    + classRoom.getName()
                    + " ";
        }
    }
}
