package com.example.smart_task_management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.example.smart_task_management.DTOs.NotificationDTO;
import com.example.smart_task_management.DTOs.TaskDTO;
import com.example.smart_task_management.DTOs.UserDTO;
import com.example.smart_task_management.Entities.Notification;
import com.example.smart_task_management.Repositories.NotificationRepository;
import com.example.smart_task_management.Services.NotificationService;
import com.example.smart_task_management.Util.Mailer;
import com.example.smart_task_management.websocket.WebSocketSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
public class NotificationServiceTest {

    @Mock
    private WebSocketSender webSocketSender;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private Mailer mailer;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendReminderNotification_TaskWithoutExistingNotification_SendsNotification() {
        TaskDTO taskDTO = createTaskDTO();
        when(notificationRepository.findByTaskIdAndUserIdAndType(taskDTO.getId(), taskDTO.getCreator().getId(), "REMINDER")).thenReturn(null);

        notificationService.sendReminderNotification(taskDTO);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(webSocketSender, times(1)).sendMessageToDestination(eq("/queue/notifications/" + taskDTO.getCreator().getId()), any(NotificationDTO.class));
        verify(mailer, times(1)).sendEmail(eq(taskDTO.getCreator().getEmail()), contains("Reminder for Task"), anyString());
    }

    @Test
    public void testSendCreationNotification() {
        TaskDTO taskDTO = createTaskDTO();

        notificationService.sendCreationNotification(taskDTO);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(webSocketSender, times(1)).sendMessageToDestination(eq("/queue/notifications/" + taskDTO.getCreator().getId()), any(NotificationDTO.class));
    }

    @Test
    public void testSendCompletionNotification() {
        TaskDTO taskDTO = createTaskDTO();

        notificationService.sendCompletionNotification(taskDTO);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(webSocketSender, times(1)).sendMessageToDestination(eq("/queue/notifications/" + taskDTO.getCreator().getId()), any(NotificationDTO.class));
    }

    @Test
    public void testSendDeletionNotification() {
        TaskDTO taskDTO = createTaskDTO();

        notificationService.sendDeletionNotification(taskDTO);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(webSocketSender, times(1)).sendMessageToDestination(eq("/queue/notifications/" + taskDTO.getCreator().getId()), any(NotificationDTO.class));
    }

    @Test
    public void testSendReminderNotification_WithExistingNotification_DoesNotSendDuplicate() {
        TaskDTO taskDTO = createTaskDTO();
        Notification existingNotification = new Notification();
        when(notificationRepository.findByTaskIdAndUserIdAndType(taskDTO.getId(), taskDTO.getCreator().getId(), "REMINDER")).thenReturn(existingNotification);

        notificationService.sendReminderNotification(taskDTO);

        verify(notificationRepository, never()).save(any(Notification.class));
        verify(webSocketSender, never()).sendMessageToDestination(anyString(), any(NotificationDTO.class));
        verify(mailer, never()).sendEmail(anyString(), anyString(), anyString());
    }

    private TaskDTO createTaskDTO() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setDeadline(LocalDateTime.now().plusDays(1));

        // Set creator information for the task
        UserDTO creator = new UserDTO();
        creator.setId(1L);
        creator.setEmail("creator@example.com");
        taskDTO.setCreator(creator);

        return taskDTO;
    }
}
