package com.example.smart_task_management.Controllers;


import com.example.smart_task_management.Entities.Notification;
import com.example.smart_task_management.Entities.Task;
import com.example.smart_task_management.Security.LoggedInUserDetails;
import com.example.smart_task_management.Services.NotificationService;

import com.example.smart_task_management.Services.TaskService;
import com.example.smart_task_management.Util.UtilClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    TaskService taskService;

    @Autowired
    UtilClass util;

    @MessageMapping({"/notifications/pending/my"})
    public void requestPendingNotifications(Authentication authentication) {



        if(util.getUserDetailsFromAuthentication(authentication) == null) {
            return;
        }

        LoggedInUserDetails notificationRequester = util.getUserDetailsFromAuthentication(authentication);
        List<Notification> notifications = notificationService.getPendingNotificationsOfUser(notificationRequester.getUserId());

        for (Notification notification : notifications) {
            Task task = taskService.getTaskById(notification.getTaskId());
            notificationService.sendInAppNotification(notification,taskService.getDTOFromTask(task));
        }

    }

    @MessageMapping("/notifications/set_seen/{notificationId}")
    public void setNotificationAsSeen(@DestinationVariable("notificationId") String notificationId, Authentication authentication) {

        if(util.getUserDetailsFromAuthentication(authentication) == null) {
            return;
        }

        LoggedInUserDetails notificationRequester = util.getUserDetailsFromAuthentication(authentication);



        Notification notification = notificationService.findNotificationById(Long.parseLong(notificationId));

        if(!notification.getUserId().equals(notificationRequester.getUserId()))
            return;


        notificationService.handleNotificationSeen(Long.parseLong(notificationId));
    }


}

