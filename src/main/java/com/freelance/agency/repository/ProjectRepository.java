package com.freelance.agency.repository;

import com.freelance.agency.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByActiveTrue();

    @Query("SELECT p FROM Project p JOIN p.tags t WHERE t IN :tags AND p.active = true")
    List<Project> findByTagsIn(@Param("tags") List<String> tags);
}