package com.rau.bot.repository.user;

import com.rau.bot.entity.user.Course;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CourseRepository extends Neo4jRepository<Course, Long> {
    Course findByName(String name);
}
