package com.example.smart_task_management.Repositories;

import com.example.smart_task_management.Entities.Task;
import com.example.smart_task_management.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserAndCompleted(User user, boolean completed);
    List<Task> findByTitleContainingOrDescriptionContaining(String title, String description);

    List<Task> findAllByUserIdOrderByCompleted(Long userId);

    List<Task> findAllByTitleContainsOrDescriptionContainsAndUserId(String title, String description,Long userId);

    List<Task> findAllByTitleContainsAndDescriptionContainsAndUserId(String title, String description,Long userId);

    int countAllByUserId(Long userId);

    int countAllByUserIdAndCompleted(Long userId, boolean completed);
}
