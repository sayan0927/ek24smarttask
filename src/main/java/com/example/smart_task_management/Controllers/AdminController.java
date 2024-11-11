package com.example.smart_task_management.Controllers;

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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Controller class responsible for handling admin-related requests.
 */
@Tag(name = "Admin", description = "Admin management APIs")
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UtilClass utilClass;

    @Autowired
    UserServiceImpl userService;

    /**
     * Handles the request to display the admin page.
     *
     * @param authentication the authentication object containing the logged-in user's details.
     * @return a ModelAndView object containing the view name "admin" and data related to the user and users list.
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string",format = "string"))
    @Operation(summary = "Get Admin Page", description = "Displays the admin page with user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin page displayed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/page")
    public ModelAndView adminPage(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("admin");
        modelAndView.addObject("user_details", utilClass.getUserDetailsFromAuthentication(authentication));

        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOS = users.stream().map((user) -> userService.getDTO(user)).toList();

        modelAndView.addObject("table_dto", userDTOS);
        return modelAndView;
    }

    /**
     * Handles the request to retrieve all users.
     * This endpoint is accessible only to admin users.
     *
     * @param authentication the authentication object containing the logged-in user's details.
     * @return a ResponseEntity containing a list of UserDTO objects and an HTTP status, or an unauthorized message if access is denied.
     */
    @Parameter(name = "JSESSIONID", in = ParameterIn.COOKIE, required = true, description = "Session ID cookie for authentication",schema = @Schema(type = "string"))
    @Operation(summary = "Get All Users", description = "Retrieves all users in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all users retrieved"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping("/all_users")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);

        if (!loggedInUserDetails.isAdmin())
            return new ResponseEntity<>("Unauthorized Access", HttpStatus.FORBIDDEN);

        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOS = users.stream().map((user) -> userService.getDTO(user)).toList();

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }
}
