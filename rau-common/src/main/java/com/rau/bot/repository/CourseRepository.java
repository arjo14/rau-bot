package com.rau.bot.repository;

import com.rau.bot.entity.Course;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CourseRepository extends Neo4jRepository<Course, Long> {
}
