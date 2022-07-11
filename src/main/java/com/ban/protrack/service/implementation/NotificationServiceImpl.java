package com.ban.protrack.service.implementation;

import com.ban.protrack.model.Project;
import com.ban.protrack.model.Work;
import com.ban.protrack.repository.ProjectRepository;
import com.ban.protrack.model.Notification;
import com.ban.protrack.model.User;
import com.ban.protrack.repository.NotificationRepository;
import com.ban.protrack.repository.UserRepository;
import com.ban.protrack.repository.WorkRepository;
import com.ban.protrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.tags.NestedPathTag;

import javax.transaction.Transactional;
import java.nio.file.Paths;
import java.util.Collection;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@RequiredArgsConstructor
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;

    private final UserRepository userRepo;

    private final ProjectRepository projectRepo;

    private final WorkRepository workRepo;

//    sowwi
    private final FilesStorageServiceImpl filesStorageService;

    @Override
    public Notification create(Long user_id, String message) {
        User user = userRepo.getById(user_id);
        return notificationRepo.save(new Notification(user, message));
    }

    @Override
    public Collection<Notification> getNotificationsOfUser(String username) {
        return notificationRepo.getNotificationsByUser(userRepo.findByUsername(username).getId());
    }

    @Override
    public Collection<Notification> getUnreadNotificationsOfUser(String username) {
        return notificationRepo.getUnreadNotificationsByUser(userRepo.findByUsername(username).getId());
    }

    @Override
    public Integer readNotification(Long notification_id, String username, Integer status) {
        Notification notification = notificationRepo.getById(notification_id);
        if (notification.isRead())
            return 0;
        User user = userRepo.findByUsername(username);
        if (user == null)
            return -1;
        if (status == 1) {
            if (notification.getType() != null && notification.getType().contains("invitation")) {
                Long project_id = Long.valueOf(notification.getType().split("/")[1]);
                if (projectRepo.getUserRole(user.getId(), project_id) != null) {
                    notification.setRead(TRUE);
                    notificationRepo.save(notification);
                    return -2;
                }
                projectRepo.addUsertoProject(user.getId(), project_id, "ROLE_USER");
                Long sender_id = Long.valueOf(notification.getType().split("/")[2]);
                User sender = userRepo.getById(sender_id);
                notificationRepo.save(new Notification(sender, username + " accepted your invitation."));
            }
//            else if (notification.getType().contains("approval")) {
//                Long project_id = Long.valueOf(notification.getType().split("/")[1]);
//                String work_id = notification.getType().split("/")[2];
//                Long sender_id = Long.valueOf(notification.getType().split("/")[3]);
//                User sender = userRepo.getById(sender_id);
//                Work work = workRepo.getWorkByProject(project_id, work_id);
//                work.setApproved(true);
//                workRepo.save(work);
//                notificationRepo.save(new Notification(sender, username + " accepted your work."));
//            }
        }
        else if (status == -1) {
            if (notification.getType() != null && notification.getType().contains("invitation")) {
                Long sender_id = Long.valueOf(notification.getType().split("/")[2]);
                User sender = userRepo.getById(sender_id);
                notificationRepo.save(new Notification(sender, username + " declined your invitation."));
            }
//            else if (notification.getType().contains("approval")) {
//                Long sender_id = Long.valueOf(notification.getType().split("/")[3]);
//                User sender = userRepo.getById(sender_id);
//                Long project_id = Long.valueOf(notification.getType().split("/")[1]);
//                String work_id = notification.getType().split("/")[2];
//                Work work = workRepo.getWorkByProject(project_id, work_id);
//                filesStorageService.delete(work.getProof());
//                work.setProof(null);
//                workRepo.save(work);
//                notificationRepo.save(new Notification(sender, username + " declined your work. Please review it."));
//            }
        }
        notification.setRead(TRUE);
        notification.setStatus(status);
        notificationRepo.save(notification);
        return 1;
    }

    public Boolean readAllNotification(String username) {
        notificationRepo.markAllAsRead(userRepo.findByUsername(username).getId());
        return TRUE;
    }

    @Override
    public Boolean deleteById(Long id) {
        notificationRepo.deleteById(id);
        return TRUE;
    }

    public boolean createInvitation(Long project_id, String sender, String username) {
        User user_sender = userRepo.findByUsername(sender);
        User user = userRepo.findByUsername(username);
        if (user == null)
            return false;
        Project project = projectRepo.getById(project_id);
        String message = user_sender.getUsername() + " invited you to join project: " + project.getName();
        Notification notification = new Notification(user, message, "invitation/" + project_id + "/" + user_sender.getId(), 0);
//        System.out.println(notification);
        notificationRepo.save(notification);
        return true;
    }
}
