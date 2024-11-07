package com.example.smart_task_management.DTOs;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class NotificationDTO {


    private Long id;

    TaskDTO taskDTO;

    Long userId;

    String type;

    String message;
}
