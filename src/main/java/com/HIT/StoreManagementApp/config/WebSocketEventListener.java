package com.HIT.StoreManagementApp.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    // Store active user sessions in this map
    private Map<String, Object> userSessions = new ConcurrentHashMap<>();

    // Listen for WebSocket connection events (user connects)
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        String username = event.getUser().getName();  // Get the username from the event
        userSessions.put(username, event);  // Add the user to the map
        System.out.println("User connected: " + username);

        // Log the current state of the user sessions map
        System.out.println("Current user sessions: " + userSessions);
    }

    // Optionally, listen for WebSocket disconnection events (user disconnects)
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
}
