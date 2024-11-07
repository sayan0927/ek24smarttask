package com.example.smart_task_management.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="role")
@Data
public class Role {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "role_name",unique = true)
    String roleName;
}
