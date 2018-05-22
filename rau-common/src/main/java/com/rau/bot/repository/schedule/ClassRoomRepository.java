package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.ClassRoom;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ClassRoomRepository extends Neo4jRepository<ClassRoom, Long> {
    ClassRoom findByName(String name);
}
