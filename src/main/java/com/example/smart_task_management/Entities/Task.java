package com.example.smart_task_management.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

@Entity
@Data
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime deadline;

    private byte priority;

    private LocalDateTime reminder;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public boolean getCompleted() {
        return completed;
    }

    @Transient
    public String getPriorityAsString()
    {
        if(priority == 0)
            return "High";
        if(priority == 1)
            return "Medium";
        else
            return "Low";
    }

}

