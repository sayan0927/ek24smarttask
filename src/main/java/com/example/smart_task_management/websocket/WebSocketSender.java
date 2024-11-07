package com.example.smart_task_management.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * A utility class for sending messages via WebSocket to specified destinations.
 * This class uses the Spring {@link SimpMessagingTemplate} to send messages to
 * subscribers connected to a WebSocket endpoint.
 */
@Component
public class WebSocketSender {

    // Injecting the SimpMessagingTemplate to facilitate message sending
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Sends a message to a specified WebSocket destination.
     *
     * This method sends an object message to a WebSocket destination. The message
     * is automatically converted into a format suitable for transmission (usually JSON).
     *
     * @param destination the destination address where the message should be sent (e.g., "/queue/notifications/{userId}")
     * @param message the message to send to the destination
     */
    public void sendMessageToDestination(String destination, Object message) {
        // Sending the message to the specified destination
        messagingTemplate.convertAndSend(destination, message);
    }
}
