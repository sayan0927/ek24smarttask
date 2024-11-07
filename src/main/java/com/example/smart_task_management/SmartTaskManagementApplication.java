package com.example.smart_task_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartTaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTaskManagementApplication.class, args);
    }

}
