package com.example.smart_task_management.Util;

import com.example.smart_task_management.Security.LoggedInUserDetails;
import com.example.smart_task_management.Services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class that provides helper methods for extracting user details
 * from the authentication context. It helps in retrieving the current logged-in user.
 */
@Component
public class UtilClass {

    // Service for accessing user information
    @Autowired
    UserServiceImpl userService;

    /**
     * Extracts the user details from the given authentication object.
     * This method retrieves the principal (i.e., the logged-in user) and
     * returns a {@link LoggedInUserDetails} object containing the user's information.
     *
     * @param authentication the authentication object representing the current user session
     * @return a {@link LoggedInUserDetails} object containing the user details
     */
    public LoggedInUserDetails getUserDetailsFromAuthentication(Authentication authentication) {
        // Extract user details (principal) from authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Debugging: Output user details to console (can be removed later)
        System.out.println("this " + userDetails);

        // Retrieve the full user details using the username
        LoggedInUserDetails loggedInUserDetails = new LoggedInUserDetails(userService.findByUserName(userDetails.getUsername()));

        return loggedInUserDetails;
    }
}
