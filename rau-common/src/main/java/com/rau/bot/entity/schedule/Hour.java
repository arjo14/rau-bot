package com.rau.bot.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Hour {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Hour(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name+")";
    }
}
