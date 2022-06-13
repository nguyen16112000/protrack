package com.ban.protrack.repository;

import com.ban.protrack.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(value = "INSERT INTO user_project (user_id, project_id, role) VALUES (?1, ?2, ?3)",nativeQuery = true)
    @Modifying
    @Transactional
    void addUsertoProject(Long user_id, Long project_id, String role);
}
