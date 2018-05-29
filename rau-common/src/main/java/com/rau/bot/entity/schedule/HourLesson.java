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
public class HourLesson {

    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "HAS_HOURS")
    private Hour hour;

    @Relationship(type = "HAS_LESSON")
    private Lesson lesson;

    public HourLesson(Hour hour, Lesson lesson) {
        this.hour = hour;
        this.lesson = lesson;
    }
}
