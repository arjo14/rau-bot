package com.rau.bot.repository;

import com.rau.bot.entity.Faculty;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FacultyDepartment extends Neo4jRepository<Faculty, Long> {
}
