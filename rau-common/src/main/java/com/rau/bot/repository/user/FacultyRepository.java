package com.rau.bot.repository.user;

import com.rau.bot.entity.user.Faculty;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FacultyRepository extends Neo4jRepository<Faculty, Long> {
    Faculty findByName(String name);
}
