package com.rau.bot.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RelationshipEntity(type = "HAS_LESSON")
public class LessonRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private HourLesson hourLesson;

    @EndNode
    private Lesson lesson;

    public LessonRelationship(HourLesson hourLesson, Lesson lesson) {
        this.hourLesson = hourLesson;
        this.lesson = lesson;
    }
}
