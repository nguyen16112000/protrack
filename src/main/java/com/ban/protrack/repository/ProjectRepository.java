package com.ban.protrack.repository;

import com.ban.protrack.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(value = "INSERT INTO user_project (user_id, project_id, role) VALUES (?1, ?2, ?3)",nativeQuery = true)
    @Modifying
    @Transactional
    void addUsertoProject(Long user_id, Long project_id, String role);

    @Query(value = "SELECT p.*, up.role FROM project p INNER JOIN user_project up ON p.id = up.project_id WHERE up.user_id = ?1", nativeQuery = true)
    List<Project> getProjectsByUser(Long user_id);

    @Query(value = "SELECT up.role FROM user_project up WHERE up.user_id = ?1 AND up.project_id = ?2", nativeQuery = true)
    String getUserRole(Long user_id, Long project_id);

    @Query(value = "SELECT up.user_id FROM user_project up WHERE up.role = 'ROLE_ADMIN' AND up.project_id = ?1", nativeQuery = true)
    Long getProjectAdmin(Long project_id);
}
