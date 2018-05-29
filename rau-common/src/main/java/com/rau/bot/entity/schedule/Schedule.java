package com.rau.bot.entity.schedule;

import com.rau.bot.entity.user.Course;
import com.rau.bot.entity.user.Faculty;
import com.rau.bot.entity.user.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Schedule {

    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "HAS_COURSE")
    private Course course;

    @Relationship(type = "HAS_GROUP")
    private Group group;

    @Relationship(type = "HAS_FACULTY")
    private Faculty faculty;

    @Relationship(type = "HAS_WEEK_DAY_LESSON")
    private List<WeekDayLessonRelationship> weekDayLessons;

}
