package com.ban.protrack.repository;

import com.ban.protrack.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, String> {
}