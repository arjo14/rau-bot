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
@RelationshipEntity(type = "HAS_WEEK_DAY_LESSON")
public class WeekDayLessonRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Schedule schedule;

    @EndNode
    private WeekDayLesson weekDayLesson;

    public WeekDayLessonRelationship(Schedule schedule, WeekDayLesson weekDayLesson) {
        this.schedule = schedule;
        this.weekDayLesson = weekDayLesson;
    }
}
