package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.Hour;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface HourRepository extends Neo4jRepository<Hour, Long> {
    List<Hour> findAll();

    Hour findByName(String name);
}
