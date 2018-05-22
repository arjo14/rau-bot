package com.rau.bot.repository.user;

import com.rau.bot.entity.user.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserRepository extends Neo4jRepository<User,Long> {
}
