package com.example.smart_task_management.Controllers;

import com.example.smart_task_management.DTOs.TaskDTO;
import com.example.smart_task_management.Entities.Task;
import com.example.smart_task_management.Security.LoggedInUserDetails;
import com.example.smart_task_management.Services.TaskService;
import com.example.smart_task_management.Util.UtilClass;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controller class for handling task-related HTTP requests.
 * Provides endpoints for creating, updating, deleting, retrieving,
 * and searching tasks. Also supports displaying task-related
 * information through views.
 */
@Controller
@RequestMapping("/tasks")
@Tag(name = "Task Management", description = "Task creation, retrieval, update, completion, search and deletion operations")
public class TaskController {

    @Autowired
    UtilClass utilClass;

    @Autowired
    TaskService taskService;

    /**
     * Creates a new task based on the provided TaskDTO object.
     *
     * @param taskDTO the TaskDTO object containing task details
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with the created task and HTTP status OK
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Create Task", description = "Creates a new task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDTO taskDTO, Authentication authentication) {

        if(!taskDTO.isDeadlineValid())
            return new ResponseEntity<>("Deadline us not valid", HttpStatus.BAD_REQUEST);

        if (!taskDTO.isReminderValid()) {
            return new ResponseEntity<>("Reminder is not valid", HttpStatus.BAD_REQUEST);
        }

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDeadline(taskDTO.getDeadline());
        task.setPriority((byte) taskDTO.getPriority());
        task.setReminder(taskDTO.getReminder());
        task.setCompleted(false);
        task.setUser(utilClass.getUserDetailsFromAuthentication(authentication).getUser()); // Set authenticated user
        Task createdTask = taskService.createTask(task);

        TaskDTO dto = taskService.getDTOFromTask(createdTask);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with the task details or HTTP status NOT_FOUND if not found,
     *         or HTTP status FORBIDDEN if the user is not authorized to access the task
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Get Task", description = "Retrieves a task by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id, Authentication authentication) {

        Task task = taskService.getTaskById(id);

        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        if (!loggedInUserDetails.getUserId().equals(task.getUser().getId())) {
            return new ResponseEntity<>("Unauthorized Access", HttpStatus.FORBIDDEN);
        }

        TaskDTO dto = taskService.getDTOFromTask(task);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Retrieves the count of tasks for a user, optionally filtered by completion status.
     *
     * @param userId the ID of the user
     * @param complete optional completion status filter (true for completed, false for pending)
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with the task count or HTTP status FORBIDDEN if the user is not authorized
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Get Task Count", description = "Retrieves task count for a user, filtered by completion status if provided.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task count retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping("/count/{userId}")
    public ResponseEntity<?> getTasksCount(@PathVariable Long userId, @RequestParam(name = "complete", required = false) Boolean complete, Authentication authentication) {

        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        if (!loggedInUserDetails.getUserId().equals(userId)) {
            return new ResponseEntity<>("Unauthorized Access", HttpStatus.FORBIDDEN);
        }

        int count;

        if (complete != null)
            count = taskService.countTotalTasksOfUserAndCompletion(userId, complete);
        else
            count = taskService.countTotalTasksOfUser(userId);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    /**
     * Searches for tasks based on various filters like title, description, status, and priority.
     *
     * @param userId the ID of the user
     * @param title optional task title filter
     * @param description optional task description filter
     * @param status optional task status filter
     * @param priority optional task priority filter
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with the list of filtered tasks
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Search Tasks", description = "Searches tasks based on various filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping("/search/{userId}")
    public ResponseEntity<?> searchTasks(
            @PathVariable Long userId,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "priority", required = false) String priority,
            Authentication authentication) {

        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        if (!loggedInUserDetails.getUserId().equals(userId)) {
            return new ResponseEntity<>("Unauthorized Access", HttpStatus.FORBIDDEN);
        }



        List<Task> tasks;

        if (title.isEmpty() && description.isEmpty())
            tasks = taskService.getTasksOfUser(loggedInUserDetails.getUserId());
        else
            tasks = taskService.getUsersTasksByDescriptionOrTitle(description, title, loggedInUserDetails.getUserId());

        if (!status.isEmpty())
            tasks = taskService.filterTasksByStatus(tasks, !status.equals("0"));

        if (!priority.isEmpty())
            tasks = taskService.filterTasksByPriority(tasks, Integer.parseInt(priority));


        List<TaskDTO> dtoList = tasks.stream().map((tsk)->{return taskService.getDTOFromTask(tsk);}).toList();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    /**
     * Updates an existing task based on the provided task ID and TaskDTO.
     *
     * @param id the ID of the task to be updated
     * @param taskDTO the updated task details
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with the updated task or HTTP status FORBIDDEN if the user is not authorized
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Update Task", description = "Updates a task by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO, Authentication authentication) {
        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        Task existingTask = taskService.getTaskById(id);

        // Ensure that the authenticated user owns the task
        if (!loggedInUserDetails.getUserId().equals(existingTask.getUser().getId())) {
            return new ResponseEntity<>("Unauthorized update request", HttpStatus.FORBIDDEN);
        }

        if(!taskDTO.isDeadlineValid())
            return new ResponseEntity<>("Deadline us not valid", HttpStatus.BAD_REQUEST);

        if (!taskDTO.isReminderValid()) {
            return new ResponseEntity<>("Reminder is not valid", HttpStatus.BAD_REQUEST);
        }

        // Update the fields of the existing task with new data from taskDTO
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setDeadline(taskDTO.getDeadline());
        existingTask.setPriority((byte) taskDTO.getPriority());
        existingTask.setReminder(taskDTO.getReminder());
        existingTask.setUser(utilClass.getUserDetailsFromAuthentication(authentication).getUser());

        Task updatedTask = taskService.updateTask(existingTask, id);
        TaskDTO dto = taskService.getDTOFromTask(updatedTask);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to be deleted
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with HTTP status OK if the task is successfully deleted, or FORBIDDEN if unauthorized
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Delete Task", description = "Deletes a task by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {

        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        Task taskToComplete = taskService.getTaskById(id);

        if (!loggedInUserDetails.getUserId().equals(taskToComplete.getUser().getId())) {
            return new ResponseEntity<>("Unauthorized delete request", HttpStatus.FORBIDDEN);
        }

        taskService.deleteTaskById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Marks a task as completed by its ID.
     *
     * @param id the ID of the task to be marked as complete
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with the completed task or HTTP status FORBIDDEN if unauthorized
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Complete Task", description = "Marks a task as completed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task marked as completed"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long id, Authentication authentication) {

        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        Task taskToComplete = taskService.getTaskById(id);

        if (!loggedInUserDetails.getUserId().equals(taskToComplete.getUser().getId())) {
            return new ResponseEntity<>("Unauthorized Complete request", HttpStatus.FORBIDDEN);
        }

        Task completed = taskService.completeTaskById(id);
        TaskDTO dto = taskService.getDTOFromTask(completed);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Displays the tasks page with task-related statistics and a list of tasks.
     *
     * @param authentication the authentication object containing user details
     * @return ModelAndView containing the view name and task-related data
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Get Tasks Page", description = "Displays the tasks page with task-related data.")
    @GetMapping("/page")
    public ModelAndView getTasksPage(Authentication authentication) {

        ModelAndView modelAndView = new ModelAndView();
        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        int total = taskService.countTotalTasksOfUser(loggedInUserDetails.getUserId());
        int complete = taskService.countTotalTasksOfUserAndCompletion(loggedInUserDetails.getUserId(),true);
        int pending = total-complete;

        modelAndView.setViewName("tasks");
        modelAndView.addObject("user_details", loggedInUserDetails);
        modelAndView.addObject("title_key", "");
        modelAndView.addObject("description_key", "");
        modelAndView.addObject("table_dto", taskService.getTasksOfUser(loggedInUserDetails.getUserId()));
        modelAndView.addObject("total", total);
        modelAndView.addObject("complete", complete);
        modelAndView.addObject("pending", pending);
        return modelAndView;
    }

    /**
     * Displays the task update page for a specific task.
     *
     * @param taskId the ID of the task to be updated
     * @param authentication the authentication object containing user details
     * @return ModelAndView containing the view name and task details for editing
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Get Update Task Page", description = "Displays the task Update page.")
    @GetMapping("/{taskId}/update_page")
    public ModelAndView getUpdateTaskPage(@PathVariable Long taskId, Authentication authentication) {
        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        Task task = taskService.getTaskById(taskId);

        //redirect to tasks page
        if(task==null)
            return getTasksPage(authentication);

        TaskDTO taskDTO = taskService.getDTOFromTask(task);

        ModelAndView modelAndView = new ModelAndView("edit_task");
        modelAndView.addObject("taskId", taskId);
        modelAndView.addObject("taskDTO", taskDTO);
        modelAndView.addObject("user_details", loggedInUserDetails);

        return modelAndView;
    }

    /**
     * Displays the tasks page with search results based on various filters.
     *
     * @param userId the ID of the user
     * @param title optional task title filter
     * @param description optional task description filter
     * @param status optional task status filter
     * @param priority optional task priority filter
     * @param authentication the authentication object containing user details
     * @return ModelAndView containing the view name and filtered tasks
     */
    @GetMapping("/search/{userId}/page")
    public ModelAndView searchTasksPage(
            @PathVariable Long userId,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "priority", required = false) String priority,
            Authentication authentication) {

        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        if(!loggedInUserDetails.getUserId().equals(userId)) {
            return getTasksPage(authentication);
        }

        List<Task> tasks;

        if (title.isEmpty() && description.isEmpty())
            tasks = taskService.getTasksOfUser(loggedInUserDetails.getUserId());
        else
            tasks = taskService.getUsersTasksByDescriptionOrTitle(description, title, loggedInUserDetails.getUserId());

        if (!status.isEmpty())
            tasks = taskService.filterTasksByStatus(tasks, !status.equals("0"));

        if (!priority.isEmpty())
            tasks = taskService.filterTasksByPriority(tasks, Integer.parseInt(priority));

        ModelAndView modelAndView = new ModelAndView("tasks");

        modelAndView.addObject("user_details", loggedInUserDetails);
        modelAndView.addObject("table_dto", tasks);
        modelAndView.addObject("title_key", title);
        modelAndView.addObject("description_key", description);

        return modelAndView;
    }
}
