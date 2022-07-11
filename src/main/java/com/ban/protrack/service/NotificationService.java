package com.ban.protrack.service;

import com.ban.protrack.model.Notification;

import java.util.Collection;

public interface NotificationService {
    Notification create(Long user_id, String message);
    Collection<Notification> getNotificationsOfUser(String username);
    Collection<Notification> getUnreadNotificationsOfUser(String username);

    Integer readNotification(Long id, String username, Integer status);

    Boolean readAllNotification(String username);
    Boolean deleteById(Long id);
}
