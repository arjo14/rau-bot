package com.rau.bot.repository;

import com.rau.bot.entity.Department;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface DepartmentRepository extends Neo4jRepository<Department, Long> {
}
