package com.example.smart_task_management.Repositories;

import com.example.smart_task_management.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String name);
}
