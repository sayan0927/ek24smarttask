package com.example.smart_task_management.Repositories;

import com.example.smart_task_management.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {



    User findByUserName(String username);
    User findUserById(Long id);

    User findByEmail(String email);
}
