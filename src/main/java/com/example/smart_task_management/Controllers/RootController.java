package com.example.smart_task_management.Controllers;

import com.example.smart_task_management.DTOs.TaskDTO;
import com.example.smart_task_management.Security.LoggedInUserDetails;
import com.example.smart_task_management.Services.UserServiceImpl;
import com.example.smart_task_management.Util.UtilClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * RootController is responsible for handling the root and login page requests.
 * It manages the home page display and the login page for the application.
 * The controller integrates with the user authentication system to display user-related information.
 */
@Controller
public class RootController {

    /**
     * Service for managing user-related operations.
     */
    @Autowired
    UserServiceImpl userService;

    /**
     * Utility class for extracting user details from the authentication context.
     */
    @Autowired
    UtilClass utilClass;

    /**
     * Handles the GET request for the root path ("/") and returns the home page.
     * The home page displays information about the logged-in user and provides
     * a TaskDTO object for creating or updating tasks.
     *
     * @param authentication the authentication object containing the user details.
     * @return a ModelAndView object that contains the view name "home" along with
     *         user details and a new TaskDTO object for the frontend.
     */
    @GetMapping("/")
    ModelAndView homePage(Authentication authentication) {
        LoggedInUserDetails loggedInUserDetails = utilClass.getUserDetailsFromAuthentication(authentication);
        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user_details", loggedInUserDetails);
        modelAndView.addObject("taskDTO", new TaskDTO());
        return modelAndView;
    }

    /**
     * Handles the GET request for the login page ("/login").
     * This page serves the login interface for the user to authenticate.
     * The method does not perform any business logic related to authentication,
     * as it is handled by Spring Security.
     *
     * @return a ModelAndView object that contains the view name "login".
     */
    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }



    /**
     * Handles unauthorized access (HTTP 401).
     *
     * @return ModelAndView for the login page with an unauthorized status
     */
    @GetMapping("/401")
    public ModelAndView unauthorized() {
        ModelAndView m = new ModelAndView("login");
        m.addObject("msg", "Please Login");
        m.setStatus(HttpStatus.UNAUTHORIZED);
        return m;
    }

    /**
     * Handles access denied (HTTP 403).
     *
     * @return ModelAndView for the login page with an unauthorized status
     */
    @GetMapping("/403")
    public ModelAndView accessDenied() {
        ModelAndView m = new ModelAndView("login");
        m.addObject("msg", "Please Login with proper privileges");
        m.setStatus(HttpStatus.FORBIDDEN);
        return m;
    }
}
