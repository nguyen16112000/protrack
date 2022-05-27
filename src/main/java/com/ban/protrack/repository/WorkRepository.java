package com.ban.protrack.repository;

import com.ban.protrack.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, String> {
}