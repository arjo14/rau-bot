package com.rau.bot.repository;

import com.rau.bot.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserRespository extends Neo4jRepository<User,Long> {
}
