package com.cadence.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
            select distinct p from Project p
            left join fetch p.tasks
            left join fetch p.members
            where p.manager.id = :managerId
            """)
    List<Project> findByManagerId(Long managerId);

}
