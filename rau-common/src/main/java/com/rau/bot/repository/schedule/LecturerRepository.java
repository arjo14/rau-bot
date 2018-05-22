package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.Lecturer;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface LecturerRepository extends Neo4jRepository<Lecturer, Long> {
    Lecturer findByName(String name);
}
