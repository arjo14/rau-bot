package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.Schedule;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface ScheduleRepository extends Neo4jRepository<Schedule, Long> {
    List<Schedule> findAll();
}
