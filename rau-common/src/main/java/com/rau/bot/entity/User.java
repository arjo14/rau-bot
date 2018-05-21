package com.rau.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class User {

    @GraphId
    private Long id;

    private String userId;
    private String fullName;
    private String email;

    @Relationship(type = "IS_IN", direction = Relationship.INCOMING)
    private Department department;

    @Relationship(type = "IS_IN", direction = Relationship.INCOMING)
    private Faculty faculty;

    @Relationship(type = "IS_IN", direction = Relationship.INCOMING)
    private Course course;

    private boolean isArmenianSector;

}
