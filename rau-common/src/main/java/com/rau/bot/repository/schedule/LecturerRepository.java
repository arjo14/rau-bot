package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.Lecturer;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface LecturerRepository extends Neo4jRepository<Lecturer, Long> {
    Lecturer findByName(String name);

    List<Lecturer> findAll();
}
