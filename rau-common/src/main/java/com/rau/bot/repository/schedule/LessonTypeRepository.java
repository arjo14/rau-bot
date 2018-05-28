package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.LessonType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface LessonTypeRepository extends Neo4jRepository<LessonType, Long> {
    LessonType findByName(String name);

    List<LessonType> findAll();
}
