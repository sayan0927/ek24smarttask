package com.example.smart_task_management;

import com.example.smart_task_management.DTOs.ProfileDetailsDTO;
import com.example.smart_task_management.Entities.Role;
import com.example.smart_task_management.Entities.User;
import com.example.smart_task_management.Repositories.UserRepository;
import com.example.smart_task_management.Services.RoleService;
import com.example.smart_task_management.Services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("dev")
public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        ProfileDetailsDTO dto = new ProfileDetailsDTO();
        dto.setEmail("test@gmail.com");
        dto.setPassword("password");
        dto.setUserName("testUser");
        dto.setFullName("Test User");


        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setPassword(passwordEncoder.encode("password"));


        Role userRole = new Role();
        userRole.setRoleName("USER");



        when(roleService.getUserRole()).thenReturn(userRole);
        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(dto);

        System.out.println(result);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    public void testUsernameExists() {
        String username = "existingUser";
        when(userRepo.findByUserName(username)).thenReturn(new User());

        assertTrue(userService.usernameExists(username));
        verify(userRepo, times(1)).findByUserName(username);
    }

    @Test
    public void testEmailExists() {
        String email = "existing@example.com";
        when(userRepo.findByEmail(email)).thenReturn(new User());

        assertTrue(userService.emailExists(email));
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        User user = new User();
        user.setUserName("testUser");
        when(userRepo.findByUserName("testUser")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepo.findByUserName("nonExistentUser")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonExistentUser"));
    }

    @Test
    public void testDeleteUserById_UserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepo.findUserById(userId)).thenReturn(user);

        userService.deleteUserById(userId);

        verify(userRepo, times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteUserById_UserDoesNotExist() {
        Long userId = 1L;
        when(userRepo.findUserById(userId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> userService.deleteUserById(userId));
    }
}

