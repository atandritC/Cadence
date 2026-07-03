package com.cadence.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
            select t from Task t
            join fetch t.project
            left join fetch t.assignee
            where t.project.id=:projectId
            """)
    List<Task> findByProjectIdWithDetails(@Param("projectId") Long projectId);
}
