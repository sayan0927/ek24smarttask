package com.example.smart_task_management.Services;

import com.example.smart_task_management.DTOs.NotificationDTO;
import com.example.smart_task_management.DTOs.TaskDTO;
import com.example.smart_task_management.Entities.Notification;
import com.example.smart_task_management.Repositories.NotificationRepository;
import com.example.smart_task_management.Util.Mailer;
import com.example.smart_task_management.websocket.WebSocketSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class responsible for sending notifications related to task events such as creation, completion,
 * deletion, and reminders. Notifications are sent via WebSocket and email.
 */
@Component
@EnableScheduling
public class NotificationService {

    private final String COMPLETE_NOTIFICATION = "COMPLETE";
    private final String DELETE_NOTIFICATION = "DELETE";
    private final String REMINDER_NOTIFICATION = "REMINDER";
    private final String CREATE_NOTIFICATION = "CREATE";

    @Autowired
    private WebSocketSender webSocketSender;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private Mailer mailer;

    public void sendInAppNotification(Notification notification,TaskDTO taskDTO) {
        webSocketSender.sendMessageToDestination("/queue/notifications/" + taskDTO.getCreator().getId(), getDTO(notification, taskDTO));
    }

    public Notification findNotificationById(Long id) {
        return notificationRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Notification id- "+id+" not found"));
    }

    public void handleNotificationSeen(Long id) {
        Notification notification = findNotificationById(id);
        notification.setSeen(true);
        notificationRepository.save(notification);
    }

    /**
     * Sends reminder notifications for multiple tasks asynchronously.
     *
     * @param tasks the list of tasks to send reminder notifications for
     */
    @Async
    public void sendReminderNotification(List<TaskDTO> tasks) {
        for (TaskDTO taskDTO : tasks) {
            sendReminderNotification(taskDTO);
        }
    }

    public List<Notification> getPendingNotificationsOfUser(Long userId) {
        return notificationRepository.findByUserIdAndSeen(userId,false);
    }

    /**
     * Converts a Notification entity and TaskDTO to a NotificationDTO.
     *
     * @param notification the Notification entity
     * @param taskDTO the TaskDTO associated with the notification
     * @return the constructed NotificationDTO
     */
    public NotificationDTO getDTO(Notification notification, TaskDTO taskDTO) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setTaskDTO(taskDTO);
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setType(notification.getType());
        notificationDTO.setId(notification.getId());
        notificationDTO.setUserId(taskDTO.getCreator().getId());
        return notificationDTO;
    }

    /**
     * Sends a reminder notification for a single task asynchronously.
     *
     * @param task the task to send a reminder notification for
     */
    @Async
    public void sendReminderNotification(TaskDTO task) {
        String type = REMINDER_NOTIFICATION;

        // Check if a reminder notification already exists
        Notification notification = notificationRepository.findByTaskIdAndUserIdAndType(task.getId(), task.getCreator().getId(), type);
        if (notification != null)
            return;

        // Create and save new notification
        Notification notification1 = createBasicNotification(task);
        notification1.setType(type);

        // Format the task's deadline and set the notification message
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = task.getDeadline();
        String formattedDateTime = dateTime.format(formatter);
        notification1.setMessage("Deadline for this task is - " + formattedDateTime);

        // Save the notification to the database
        notificationRepository.save(notification1);

        // Send notification via WebSocket
        webSocketSender.sendMessageToDestination("/queue/notifications/" + task.getCreator().getId(), getDTO(notification1, task));

        // Send reminder email
        String recipient = task.getCreator().getEmail();
        String subject = "Reminder for Task-" + task.getTitle();
        String body = "You have a pending Task\n" + "Title - " + task.getTitle() + "\nDescription - " + task.getDescription() + "\nDeadline- " + formattedDateTime;
        mailer.sendEmail(recipient, subject, body);
    }

    /**
     * Sends a task creation notification asynchronously.
     *
     * @param task the task for which a creation notification will be sent
     */
    @Async
    public void sendCreationNotification(TaskDTO task) {
        Notification notification1 = createBasicNotification(task);
        notification1.setType(CREATE_NOTIFICATION);
        notification1.setMessage("You created this Task");

        // Save and send WebSocket notification
        notificationRepository.save(notification1);
        webSocketSender.sendMessageToDestination("/queue/notifications/" + task.getCreator().getId(), getDTO(notification1, task));
    }

    /**
     * Sends a task completion notification asynchronously.
     *
     * @param task the task for which a completion notification will be sent
     */
    @Async
    public void sendCompletionNotification(TaskDTO task) {
        Notification notification1 = createBasicNotification(task);
        notification1.setType(COMPLETE_NOTIFICATION);
        notification1.setMessage("You completed the Task");

        // Save and send WebSocket notification
        notificationRepository.save(notification1);
        webSocketSender.sendMessageToDestination("/queue/notifications/" + task.getCreator().getId(), getDTO(notification1, task));
    }

    /**
     * Sends a task deletion notification asynchronously.
     *
     * @param task the task for which a deletion notification will be sent
     */
    @Async
    public void sendDeletionNotification(TaskDTO task) {
        Notification notification1 = createBasicNotification(task);
        notification1.setType(DELETE_NOTIFICATION);
        notification1.setMessage("You Deleted the Task");

        // Send WebSocket notification
        webSocketSender.sendMessageToDestination("/queue/notifications/" + task.getCreator().getId(), getDTO(notification1, task));
    }

    /**
     * Creates a basic notification for a task.
     *
     * @param taskDTO the task associated with the notification
     * @return a Notification object with the basic details
     */
    private Notification createBasicNotification(TaskDTO taskDTO) {
        Notification notification1 = new Notification();
        notification1.setTaskId(taskDTO.getId());
        notification1.setUserId(taskDTO.getCreator().getId());
        notification1.setSeen(false);
        return notification1;
    }
}
