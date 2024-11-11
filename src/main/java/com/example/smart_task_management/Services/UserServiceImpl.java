package com.example.smart_task_management.Services;

import com.example.smart_task_management.DTOs.ProfileDetailsDTO;
import com.example.smart_task_management.DTOs.UserDTO;
import com.example.smart_task_management.Entities.User;
import com.example.smart_task_management.Repositories.UserRepository;
import com.example.smart_task_management.Security.LoggedInUserDetails;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Service implementation for managing user-related operations such as
 * registering, updating, deleting, and retrieving users.
 * Implements UserDetailsService for Spring Security authentication.
 */
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the User object associated with the given ID
     * @throws NoSuchElementException if the user is not found
     */
    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(()->new NoSuchElementException("User id" + id + " Not Found"));
    }

    /**
     * Updates an existing user's profile details.
     *
     * @param dto the ProfileDetailsDTO containing updated user information
     * @param userId the ID of the user to update
     * @return the updated User object
     */
    public User updateUser(ProfileDetailsDTO dto, Long userId) {
        User user = userRepo.findUserById(userId);
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserName(dto.getUserName());
        user.setRoles(Set.of(roleService.getUserRole()));
        user.setFullName(dto.getFullName());
        return user;
    }

    /**
     * Registers a new user based on the provided profile details.
     *
     * @param dto the ProfileDetailsDTO containing user information
     * @return the newly created User object
     */
    public User registerUser(ProfileDetailsDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserName(dto.getUserName());
        user.setRoles(Set.of(roleService.getUserRole()));
        user.setFullName(dto.getFullName());
        return userRepo.save(user);
    }

    /**
     * Converts a User object to a UserDTO for use in API responses.
     *
     * @param user the User entity to convert
     * @return a UserDTO containing the user's details
     */
    public UserDTO getDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        return dto;
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all User objects
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Validates the given email address using an email validation library.
     *
     * @param email the email address to validate
     * @return true if the email is valid, false otherwise
     */
    public Boolean validEmailAddress(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    public Boolean usernameExists(String username) {
        return userRepo.findByUserName(username) != null;
    }

    /**
     * Checks if an email address is already associated with an existing user.
     *
     * @param email the email address to check
     * @return true if the email exists, false otherwise
     */
    public Boolean emailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }

    /**
     * Retrieves a user by their username.
     *
     * @param userName the username of the user
     * @return the User object associated with the given username
     */
    public User findByUserName(String userName) {
        return userRepo.findByUserName(userName);
    }

    /**
     * Loads a user by their username for authentication purposes.
     *
     * @param username the username of the user
     * @return the UserDetails object representing the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        return new LoggedInUserDetails(user);
    }

    /**
     * Loads a user by their email address for authentication purposes.
     *
     * @param email the email address of the user
     * @return the UserDetails object representing the user
     * @throws UsernameNotFoundException if the user is not found
     */
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        return new LoggedInUserDetails(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     * @throws NoSuchElementException if the user is not found
     */
    public void deleteUserById(Long userId) {
        if (userRepo.findUserById(userId) == null) {
            throw new NoSuchElementException("Invalid userId");
        }
        userRepo.deleteById(userId);
    }
}
