package com.example.smart_task_management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.smart_task_management.DTOs.TaskDTO;
import com.example.smart_task_management.DTOs.UserDTO;
import com.example.smart_task_management.Entities.Task;
import com.example.smart_task_management.Entities.User;
import com.example.smart_task_management.Repositories.TaskRepository;
import com.example.smart_task_management.Services.NotificationService;
import com.example.smart_task_management.Services.TaskService;
import com.example.smart_task_management.Services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("dev")
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTask_WithReminder() {
        Task task = createTask();
        task.setReminder(task.getDeadline().minusHours(1));

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(userServiceImpl.getDTO(any())).thenReturn(createUserDTO());

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        verify(taskRepository, times(1)).save(task);
        verify(notificationService, times(1)).sendCreationNotification(any(TaskDTO.class));
    }

    @Test
    public void testCreateTask_WithoutReminder() {
        Task task = createTask();
        task.setReminder(null);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setReminder(savedTask.getDeadline().minusHours(1)); // Simulate setting reminder
            return savedTask;
        });
        when(userServiceImpl.getDTO(any())).thenReturn(createUserDTO());

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        assertEquals(task.getDeadline().minusHours(1), createdTask.getReminder());
        verify(taskRepository, times(1)).save(task);
        verify(notificationService, times(1)).sendCreationNotification(any(TaskDTO.class));
    }



    @Test
    public void testCompleteTaskById() {
        Task task = createTask();
        task.setCompleted(false);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task completedTask = taskService.completeTaskById(task.getId());

        assertTrue(completedTask.getCompleted());
        verify(notificationService, times(1)).sendCompletionNotification(any(TaskDTO.class));
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testGetUsersTasksByDescriptionOrTitle() {
        Long userId = 1L;
        String description = "sample";
        String title = "sample title";
        List<Task> tasks = List.of(createTask());

        when(taskRepository.findAllByTitleContainsOrDescriptionContainsAndUserId(title, description, userId)).thenReturn(tasks);

        List<Task> resultTasks = taskService.getUsersTasksByDescriptionOrTitle(description, title, userId);

        assertEquals(1, resultTasks.size());
    }

    @Test
    public void testSendReminders() {
        Task task = createTask();
        task.setReminder(LocalDateTime.now().minusMinutes(1));
        task.setCompleted(false);

        List<Task> tasks = List.of(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        taskService.sendReminders();

        verify(notificationService, times(1)).sendReminderNotification(anyList());
    }

    @Test
    public void testDeleteTaskById() {
        Task task = createTask();
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        taskService.deleteTaskById(task.getId());

        verify(taskRepository, times(1)).deleteById(task.getId());
        verify(notificationService, times(1)).sendDeletionNotification(any(TaskDTO.class));
    }

    // Helper method to create a sample Task
    private Task createTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setUser(createUser());
        return task;
    }

    private UserDTO createUserDTO()
    {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        return user;
    }

    // Helper method to create a sample UserDTO for the task's creator
    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        return user;
    }

}
