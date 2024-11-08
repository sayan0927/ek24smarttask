package com.example.smart_task_management.Repositories;

import com.example.smart_task_management.Entities.Notification;
import com.example.smart_task_management.Entities.Role;
import com.example.smart_task_management.Entities.Task;
import com.example.smart_task_management.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {


    Notification findByTaskIdAndUserIdAndType(Long taskId, Long userId,String type);

    List<Notification> findByUserIdAndSeen(Long taskId,Boolean seen);
}
