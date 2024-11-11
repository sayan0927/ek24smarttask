package com.example.smart_task_management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.example.smart_task_management.Entities.Role;
import com.example.smart_task_management.Repositories.RoleRepository;
import com.example.smart_task_management.Services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserRole() {
        Role userRole = new Role();
        userRole.setRoleName("USER");
        when(roleRepository.findByRoleName("USER")).thenReturn(userRole);

        Role result = roleService.getUserRole();

        assertEquals("USER", result.getRoleName());
        verify(roleRepository, times(1)).findByRoleName("USER");
    }

    @Test
    public void testGetAdminRole() {
        Role adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(adminRole);

        Role result = roleService.getAdminRole();

        assertEquals("ADMIN", result.getRoleName());
        verify(roleRepository, times(1)).findByRoleName("ADMIN");
    }
}
