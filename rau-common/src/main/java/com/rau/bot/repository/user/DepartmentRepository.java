package com.rau.bot.repository.user;

import com.rau.bot.entity.user.Department;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface DepartmentRepository extends Neo4jRepository<Department, Long> {
    Department findByName(String name);
}
