package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.Lesson;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface LessonRepository extends Neo4jRepository<Lesson, Long> {
    List<Lesson> findAll();
}
