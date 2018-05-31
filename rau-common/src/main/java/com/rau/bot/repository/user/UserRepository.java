package com.rau.bot.repository.user;

import com.rau.bot.entity.user.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface UserRepository extends Neo4jRepository<User, Long> {
    List<User> findAll();

    User findByUserIdEquals(String userId);

    User findUserByFullNameEquals(String fullName);
}
