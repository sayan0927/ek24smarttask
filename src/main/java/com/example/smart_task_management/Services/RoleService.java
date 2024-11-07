package com.example.smart_task_management.Services;

import com.example.smart_task_management.Entities.Role;
import com.example.smart_task_management.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing roles in the system,
 * providing methods to retrieve roles like "USER" and "ADMIN".
 */
@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Retrieves the role associated with "USER".
     *
     * @return the Role object corresponding to the "USER" role
     */
    public Role getUserRole() {
        return roleRepository.findByRoleName("USER");
    }

    /**
     * Retrieves the role associated with "ADMIN".
     *
     * @return the Role object corresponding to the "ADMIN" role
     */
    public Role getAdminRole() {
        return roleRepository.findByRoleName("ADMIN");
    }
}
