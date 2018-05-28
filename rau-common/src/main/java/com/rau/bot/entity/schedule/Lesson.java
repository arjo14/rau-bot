package com.rau.bot.entity.schedule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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
}
