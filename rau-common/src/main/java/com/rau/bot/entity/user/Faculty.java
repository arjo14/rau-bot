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
public class Faculty {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = "IS_IN")
    private Department department;

    public Faculty(String name, Department department) {
        this.name = name;
        this.department = department;
    }
}
