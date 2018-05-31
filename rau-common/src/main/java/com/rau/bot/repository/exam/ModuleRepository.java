package com.rau.bot.repository.exam;

import com.rau.bot.entity.exams.Module;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface ModuleRepository extends Neo4jRepository<Module, Long> {
    List<Module> findAll();

    List<Module> findAll(int i);
}
