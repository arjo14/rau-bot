package com.rau.bot.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class WeekDayLesson {

    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "HAS_WEEKDAY")
    private WeekDay weekDay;

    @Relationship(type = "HAS_HOUR_LESSON")
    private List<HourLessonRelationship> hourLessons;

    public WeekDayLesson(WeekDay weekDay, List<HourLessonRelationship> hourLessons) {
        this.weekDay = weekDay;
        this.hourLessons = hourLessons;
    }
}
