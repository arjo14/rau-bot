package com.rau.bot.repository.schedule;

import com.rau.bot.entity.schedule.Schedule;
import com.rau.bot.entity.user.Course;
import com.rau.bot.entity.user.Faculty;
import com.rau.bot.entity.user.Group;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends Neo4jRepository<Schedule, Long> {
    List<Schedule> findAll();
    List<Schedule> findAll(int i);
    List<Schedule> findAllByArmenianSectorEqualsAndFromFirstPartEquals(Boolean armenianSector, Boolean fromFirstPart);

    @Query("MATCH (n:Schedule) WHERE n.course = {course} AND n.faculty = {faculty} AND n.group = {group} AND n.fromFirstPart = {fromFirstPart} AND n.armenianSector = {armenianSector} RETURN n")
    Schedule findByFilter1(@Param("course") Course course,
                           @Param("faculty") Faculty faculty,
                           @Param("group") Group group,
                           @Param("fromFirstPart") Boolean fromFirstPart,
                           @Param("armenianSector") Boolean armenianSector);

    @Query("MATCH (s:Schedule)-[r:HAS_COURSE]->(c:Course) WHERE c.id= {courseId} RETURN s ")
    Schedule findByFilter2(@Param("courseId") Long courseId);

    Schedule findByCourseIdEquals(Long courseId);

    Schedule findByFromFirstPartEquals(Boolean fromFirstPart);
}
