package com.HIT.StoreManagementApp.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    // Store active user sessions in this map, with the username as the key and session ID as the value
    private Map<String, String> userSessions = new ConcurrentHashMap<>();

    // Store missed messages in this map, with the username as the key and list of messages as the value
    private Map<String, List<String>> missedMessages = new ConcurrentHashMap<>();

    // Listen for WebSocket connection events (user connects)
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = event.getUser().getName();  // Get the username from the event

        // Store session ID with the username
        userSessions.put(username, headerAccessor.getSessionId());

        // Log user connection
        System.out.println("User connected: " + username);

        // Send missed messages if there are any
        if (missedMessages.containsKey(username)) {
            List<String> messages = missedMessages.get(username);
            for (String message : messages) {
                // Send each missed message to the user
                // This assumes you have a method to send messages via WebSocket
                sendMessageToUser(username, message);
            }
            // Clear missed messages after sending
            missedMessages.remove(username);
        }

        // Log the current state of the user sessions map
        System.out.println("Current user sessions: " + userSessions);
    }

    // Listen for WebSocket disconnection events (user disconnects)
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            userSessions.remove(username);  // Remove the user from the map when they disconnect
            System.out.println("User disconnected: " + username);
        }

        // Log the current state of the user sessions map after disconnection
        System.out.println("Current user sessions after disconnection: " + userSessions);
    }

    // Method to store missed messages when the user is not connected
    public void storeMissedMessage(String username, String message) {
        missedMessages.computeIfAbsent(username, k -> new ArrayList<>()).add(message);
    }

    // Method to send messages to the user via WebSocket
    private void sendMessageToUser(String username, String message) {
        // Assuming you have a method to send a message to the user via their session
        // e.g., messagingTemplate.convertAndSendToUser(username, "/queue/messages", message);
        System.out.println("Sending missed message to " + username + ": " + message);
    }
}
