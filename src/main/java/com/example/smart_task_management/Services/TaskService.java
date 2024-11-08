package com.example.smart_task_management.Services;

import com.example.smart_task_management.DTOs.TaskDTO;
import com.example.smart_task_management.Entities.Task;
import com.example.smart_task_management.Repositories.TaskRepository;
import com.example.smart_task_management.Util.Mailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing task-related operations such as
 * creating, updating, deleting tasks, sending reminders, and generating notifications.
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    /**
     * Creates a new task and sends a notification for the task creation.
     *
     * @param task the Task object to be created
     * @return the created Task object
     */
    public Task createTask(Task task) {
        if (task.getReminder() == null) {
            task.setReminder(task.getDeadline().minusHours(1));
        }
        task = taskRepository.save(task);
        TaskDTO dto = getDTOFromTask(task);

        notificationService.sendCreationNotification(dto);
        return task;
    }

    /**
     * Marks a task as completed and sends a notification for task completion.
     *
     * @param taskId the ID of the task to mark as completed
     * @return the updated Task object with the completed status
     */
    public Task completeTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId).get();

        task.setCompleted(true);
        TaskDTO dto = getDTOFromTask(task);
        notificationService.sendCompletionNotification(dto);
        return taskRepository.save(task);
    }

    /**
     * Retrieves tasks based on the title or description for a specific user.
     *
     * @param description the description to search for
     * @param title the title to search for
     * @param userId the ID of the user whose tasks are to be fetched
     * @return a list of tasks matching the given criteria
     */
    public List<Task> getUsersTasksByDescriptionOrTitle(String description, String title, Long userId) {
        if (description.isEmpty()) description = null;
        if (title.isEmpty()) title = null;

        return taskRepository.findAllByTitleContainsOrDescriptionContainsAndUserId(title, description, userId);
    }

    /**
     * Filters the tasks based on the specified priority level.
     *
     * @param tasks the list of tasks to be filtered
     * @param priority the priority level to filter tasks by
     * @return a list of tasks that match the specified priority level
     */
    public List<Task> filterTasksByPriority(List<Task> tasks, int priority) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPriority() == priority) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    /**
     * Updates an existing task with the given task data and task ID.
     *
     * @param task the updated Task object
     * @param taskId the ID of the task to update
     * @return the updated Task object
     */
    public Task updateTask(Task task, Long taskId) {
        task.setId(taskId);
        if (task.getReminder() == null) {
            task.setReminder(task.getDeadline().minusHours(1));
        }
        return taskRepository.save(task);
    }

    /**
     * Sends reminders for tasks whose reminder time has passed
     * This method runs on a scheduled interval to notify users of pending tasks.
     */
    @Scheduled(fixedRate = 3000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = getAllTasks();
        List<TaskDTO> toNotify = new ArrayList<>();

        for (Task task : tasks) {
            System.out.println("time is"+now+" "+task.getReminder());
            if (!task.getCompleted() && task.getReminder().isBefore(now)) {
                toNotify.add(getDTOFromTask(task));
                System.out.println("added");
            }
        }

        notificationService.sendReminderNotification(toNotify);
    }

    /**
     * Counts the total number of tasks assigned to a user.
     *
     * @param userId the ID of the user
     * @return the total count of tasks for the user
     */
    public int countTotalTasksOfUser(Long userId) {
        return taskRepository.countAllByUserId(userId);
    }

    /**
     * Counts the total number of tasks assigned to a user, filtered by completion status.
     *
     * @param userId the ID of the user
     * @param completed the completion status (true for completed, false for pending)
     * @return the total count of tasks based on completion status
     */
    public int countTotalTasksOfUserAndCompletion(Long userId, boolean completed) {
        return taskRepository.countAllByUserIdAndCompleted(userId, completed);
    }

    /**
     * Filters the tasks based on their completion status.
     *
     * @param tasks the list of tasks to be filtered
     * @param status the completion status to filter by
     * @return a list of tasks that match the specified completion status
     */
    public List<Task> filterTasksByStatus(List<Task> tasks, boolean status) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getCompleted() == status) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    /**
     * Retrieves all tasks from the task repository.
     *
     * @return a list of all tasks
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Retrieves all tasks assigned to a specific user, ordered by completion status.
     *
     * @param userId the ID of the user
     * @return a list of tasks assigned to the user
     */
    public List<Task> getTasksOfUser(Long userId) {
        return taskRepository.findAllByUserIdOrderByCompleted(userId);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param taskId the ID of the task
     * @return the Task object associated with the given ID
     * @throws RuntimeException if the task is not found
     */
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow();
    }

    /**
     * Converts a Task object to a TaskDTO for easier handling in notifications or APIs.
     *
     * @param task the Task object to convert
     * @return the TaskDTO representation of the task
     */
    public TaskDTO getDTOFromTask(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setReminder(task.getReminder());
        taskDTO.setPriority(taskDTO.getPriority());
        taskDTO.setDeadline(task.getDeadline());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setReminder(task.getReminder());
        taskDTO.setCreator(userServiceImpl.getDTO(task.getUser()));
        return taskDTO;
    }

    /**
     * Deletes a task by its ID and sends a notification about the task deletion.
     *
     * @param taskId the ID of the task to delete
     */
    public void deleteTaskById(Long taskId) {
        Task toDel = taskRepository.findById(taskId).orElseThrow();
        TaskDTO dto = getDTOFromTask(toDel);

        taskRepository.deleteById(taskId);
        notificationService.sendDeletionNotification(dto);
    }
}
