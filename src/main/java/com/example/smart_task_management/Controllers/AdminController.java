package com.example.smart_task_management.Controllers;

import com.example.smart_task_management.Services.UserServiceImpl;
import com.example.smart_task_management.Util.UtilClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UtilClass utilClass;

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/page")
    public ModelAndView adminPage(Authentication authentication) {

        ModelAndView modelAndView = new ModelAndView("admin");
        modelAndView.addObject("user_details", utilClass.getUserDetailsFromAuthentication(authentication));
        modelAndView.addObject("table_dto", userService.getAllUsers());
        return modelAndView;


    }
}
