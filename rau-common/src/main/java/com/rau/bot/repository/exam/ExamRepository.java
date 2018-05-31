package com.rau.bot.repository.exam;

import com.rau.bot.entity.exams.Exam;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface ExamRepository extends Neo4jRepository<Exam, Long> {
    List<Exam> findAll();

    List<Exam> findAll(int i);
}
