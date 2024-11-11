package com.example.smart_task_management.Controllers;

import com.example.smart_task_management.DTOs.ProfileDetailsDTO;
import com.example.smart_task_management.DTOs.UserDTO;
import com.example.smart_task_management.Entities.User;
import com.example.smart_task_management.Security.LoggedInUserDetails;
import com.example.smart_task_management.Services.UserServiceImpl;
import com.example.smart_task_management.Util.UtilClass;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;



/**
 * The UserController is responsible for managing user-related actions, including
 * user registration, user retrieval, updating user details, and deleting users.
 * It integrates with the UserService and uses ProfileDetailsDTO for handling user profile data.
 * Security checks are implemented to ensure proper authorization for user actions.
 */
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@Controller
@RequestMapping("/users")
@Tag(name = "User Management", description = "User registration, retrieval, update, and deletion operations")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UtilClass utilClass;

    /**
     * Deletes a user by ID if the authenticated user is an admin or the user themselves.
     * Responds with appropriate status codes based on authorization and outcome.
     *
     * @param id the ID of the user to be deleted
     * @param authentication the authentication object containing the logged-in user's details
     * @return ResponseEntity with success or failure message
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Delete User", description = "Deletes a user by ID if authorized (admin or self)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "403", description = "Unauthorized delete request")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        if (!loggedInUserDetails.isAdmin() && !loggedInUserDetails.getUserId().equals(id)) {
            return new ResponseEntity<>("Unauthorized delete request", HttpStatus.FORBIDDEN);
        }
        userService.deleteUserById(id);
        return new ResponseEntity<>("User deleted", HttpStatus.OK);
    }

    /**
     * Retrieves a user by ID if the authenticated user is the same as the user being requested.
     * Responds with the user object or a forbidden message.
     *
     * @param id the ID of the user to be retrieved
     * @param authentication the authentication object containing the logged-in user's details
     * @return ResponseEntity with the user object or an error message
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Get User", description = "Retrieves a user by ID if authorized (self)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved"),
            @ApiResponse(responseCode = "403", description = "Unauthorized get request")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id, Authentication authentication) {
        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        if (!loggedInUserDetails.getUserId().equals(id)) {
            return new ResponseEntity<>("Unauthorized get request", HttpStatus.FORBIDDEN);
        }

        User user = userService.getUserById(id);
        UserDTO dto = userService.getDTO(user);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Updates user profile details for a user based on the provided ProfileDetailsDTO.
     * Ensures the user is authorized to update their own details and handles validation for
     * email, password confirmation, and username.
     *
     * @param id the ID of the user to be updated
     * @param authentication the authentication object containing the logged-in user's details
     * @param profileDetailsDTO the data to update the user's profile
     * @return ResponseEntity with success or error message
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Update User", description = "Updates profile details for a user if authorized")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "403", description = "Unauthorized update request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict in data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, Authentication authentication, @RequestBody ProfileDetailsDTO profileDetailsDTO) {
        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        if (!loggedInUserDetails.getUserId().equals(id)) {
            return new ResponseEntity<>("Unauthorized get request", HttpStatus.FORBIDDEN);
        }

        if (userService.getUserById(id) == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!profileDetailsDTO.getPassword().equals(profileDetailsDTO.getPasswordConfirm())) {
            return new ResponseEntity<>("Passwords don't match", HttpStatus.CONFLICT);
        }

        if (!userService.validEmailAddress(profileDetailsDTO.getEmail())) {
            return new ResponseEntity<>("Invalid email address", HttpStatus.BAD_REQUEST);
        }

        if (userService.emailExists(profileDetailsDTO.getEmail())) {
            return new ResponseEntity<>("Email Unavailable", HttpStatus.CONFLICT);
        }

        if (userService.usernameExists(profileDetailsDTO.getUserName())) {
            return new ResponseEntity<>("Username Unavailable", HttpStatus.CONFLICT);
        }

        User user = userService.updateUser(profileDetailsDTO, id);

        if (user == null) {
            return new ResponseEntity<>("Internal Server Error. Please try later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserDTO userDTO = userService.getDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Registers a new user based on the ProfileDetailsDTO, checking for password match, valid email,
     * and availability of the email and username. Responds with the appropriate status based on the validation result.
     *
     * @param profileDetailsDTO the data to register the new user
     * @return ResponseEntity with the status of the registration
     */
    @Operation(summary = "Register User", description = "Registers a new user with provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered"),
            @ApiResponse(responseCode = "409", description = "Conflict in data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> registerUser(@ModelAttribute("signup") ProfileDetailsDTO profileDetailsDTO) {
        if (!profileDetailsDTO.getPassword().equals(profileDetailsDTO.getPasswordConfirm())) {
            return new ResponseEntity<>("Passwords don't match", HttpStatus.CONFLICT);
        }

        if (!userService.validEmailAddress(profileDetailsDTO.getEmail())) {
            return new ResponseEntity<>("Invalid email address", HttpStatus.BAD_REQUEST);
        }

        if (userService.emailExists(profileDetailsDTO.getEmail())) {
            return new ResponseEntity<>("Email Unavailable", HttpStatus.CONFLICT);
        }

        if (userService.usernameExists(profileDetailsDTO.getUserName())) {
            return new ResponseEntity<>("Username Unavailable", HttpStatus.CONFLICT);
        }

        User user = userService.registerUser(profileDetailsDTO);

        if (user == null) {
            return new ResponseEntity<>("Internal Server Error. Please try later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserDTO userDTO = userService.getDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Returns the registration page view for users to sign up.
     *
     * @return ModelAndView for the registration page
     */
    @Operation(summary = "Register Page", description = "Returns the registration page view for signing up")
    @ApiResponse(responseCode = "200", description = "Registration page displayed")
    @GetMapping("/register/page")
    public ModelAndView registerPage() {
        ModelAndView modelAndView = new ModelAndView("registration");
        modelAndView.addObject("signup", new ProfileDetailsDTO());
        return modelAndView;
    }
}
