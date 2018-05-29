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
@RelationshipEntity(type = "HAS_HOUR_LESSON")
public class HourLessonRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private WeekDayLesson weekDayLesson;

    @EndNode
    private HourLesson hourLesson;

    public HourLessonRelationship(WeekDayLesson weekDayLesson, HourLesson hourLesson) {
        this.weekDayLesson = weekDayLesson;
        this.hourLesson = hourLesson;
    }
}
