package com.rau.bot.repository.exam;

import com.rau.bot.entity.exams.Exam;
import com.rau.bot.entity.exams.ModuleSchedule;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface ModuleScheduleRepository extends Neo4jRepository<ModuleSchedule, Long> {
    List<ModuleSchedule> findAll();

    List<ModuleSchedule> findAll(int i);

    List<ModuleSchedule> findAllByArmenianSectorEqualsAndFromFirstPartEquals(Boolean armenianSector, Boolean fromFirstPart);
}
