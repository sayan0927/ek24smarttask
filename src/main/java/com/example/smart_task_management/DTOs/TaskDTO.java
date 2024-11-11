package com.example.smart_task_management.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTO {

    private Long id;
    @NotBlank(message = "required")
    private String title;
    @NotBlank(message = "required")
    private String description;
    @NotNull
    private LocalDateTime deadline;
    @NotNull
    private int priority;


    private LocalDateTime reminder;
    private UserDTO creator;


}
