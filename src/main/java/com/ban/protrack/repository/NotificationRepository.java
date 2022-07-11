package com.ban.protrack.repository;

import com.ban.protrack.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = "SELECT u.* FROM notification u WHERE u.user_id = ?1", nativeQuery = true)
    List<Notification> getNotificationsByUser(Long user_id);

    @Query(value = "SELECT u.* FROM notification u WHERE u.user_id = ?1 and u.is_read = false", nativeQuery = true)
    List<Notification> getUnreadNotificationsByUser(Long user_id);

    @Query(value = "UPDATE notification SET is_read = true WHERE id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void markAsRead(Long id);

    @Query(value = "UPDATE notification SET is_read = true WHERE user_id = ?1 and is_read = false", nativeQuery = true)
    @Modifying
    @Transactional
    void markAllAsRead(Long user_id);
}
