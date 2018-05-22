package com.rau.bot.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;
    private String userId;
    private String email;

    private Boolean inFirstPart = true;

    @Relationship(type = "IS_IN")
    private Faculty faculty;

    @Relationship(type = "IS_IN")
    private Course course;

    @Relationship(type = "IS_IN")
    private Group group;

    private Boolean armenianSector = false;

}
