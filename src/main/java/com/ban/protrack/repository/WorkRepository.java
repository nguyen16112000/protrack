package com.ban.protrack.repository;

import com.ban.protrack.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface WorkRepository extends JpaRepository<Work, Long> {
    @Query(value = "SELECT u.* FROM Work u WHERE u.user_id = ?1", nativeQuery = true)
    List<Work> getWorksByUser(Long user_id);

    @Query(value = "SELECT u.* FROM Work u WHERE u.project_id = ?1", nativeQuery = true)
    List<Work> getWorksByProject(Long project_id);


    @Query(value = "SELECT u.* FROM Work u WHERE u.user_id = ?1 AND u.project_id = ?2", nativeQuery = true)
    List<Work> getWorksByUserProject(Long user_id, Long project_id);

    @Query(value = "SELECT u.* FROM Work u WHERE u.project_id = ?1 AND u.id = ?2", nativeQuery = true)
    Work getWorkByProject(Long project_id, String work_id);

    @Query(value = "INSERT INTO work_order (work_before_id, work_after_id) VALUES (?1, ?2)", nativeQuery = true)
    @Modifying
    @Transactional
    void addWorkOrdertoProject(String work_after_id, String work_before_id);

    @Query(value = "UPDATE Work SET user_id = ?2 WHERE id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void setWorker(String work_id, Long user_id);

    @Query(value = "UPDATE Work SET es_date = ?2, ef_date = ?3, ls_date = ?4, lf_date = ?5 WHERE id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void updateAfterEvaluate(String work_id, LocalDate es, LocalDate ef, LocalDate ls, LocalDate lf);
}
