package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.WeekDayLesson;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface WeekDayLessonRepository extends Neo4jRepository<WeekDayLesson, Long> {
    List<WeekDayLesson> findAll();
}
