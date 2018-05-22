package com.rau.bot.repository.user;

import com.rau.bot.entity.user.Group;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GroupRepository extends Neo4jRepository<Group, Long> {
    Group findByName(String name);
}
