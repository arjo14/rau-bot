package com.rau.bot.repository.exam;

import com.rau.bot.entity.exams.Exam;
import com.rau.bot.entity.exams.ExamSchedule;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface ExamScheduleRepository extends Neo4jRepository<ExamSchedule, Long> {
    List<ExamSchedule> findAll();

    List<ExamSchedule> findAll(int i);

    List<ExamSchedule> findAllByArmenianSectorEqualsAndFromFirstPartEquals(Boolean armenianSector, Boolean fromFirstPart);
}
