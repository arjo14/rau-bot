package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.Subject;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface SubjectRepository extends Neo4jRepository<Subject, Long> {
    Subject findByName(String name);

    List<Subject> findAll();
}
