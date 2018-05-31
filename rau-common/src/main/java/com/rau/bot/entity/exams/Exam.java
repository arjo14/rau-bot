package com.rau.bot.entity.exams;

import com.rau.bot.entity.schedule.ClassRoom;
import com.rau.bot.entity.schedule.Hour;
import com.rau.bot.entity.schedule.Lecturer;
import com.rau.bot.entity.schedule.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Exam {

    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "HAS_LECTURER")
    private List<Lecturer> lecturers;

    @Relationship(type = "HAS_SUBJECT")
    private Subject subject;

    @Relationship(type = "IS_IN")
    private ClassRoom classRoom;

    private String hours;

    private Date date;
}
