package com.example.smart_task_management.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    Long taskId;

    Long userId;

    String type;

    String message;
}
