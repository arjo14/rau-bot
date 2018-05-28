package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.WeekDay;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface WeekDayRepository extends Neo4jRepository<WeekDay, Long> {
    WeekDay findByName(String name);

    List<WeekDay> findAll();
}
