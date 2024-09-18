package com.HIT.StoreManagementApp.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionService {

    // Store connected users with their session ID
    private final Map<String, String> connectedUsers = new ConcurrentHashMap<>();

    // Handle user connection event
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        String username = event.getUser().getName();  // Assuming username is tied to the session
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        connectedUsers.put(sessionId, username);
        System.out.println("User connected: " + username);
    }

    // Handle user disconnection event
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String username = connectedUsers.remove(sessionId);
        System.out.println("User disconnected: " + username);
    }

    // Get list of connected users
    public Map<String, String> getConnectedUsers() {
        return connectedUsers;
    }

    // Get session ID by username
    public String getSessionIdByUsername(String username) {
        return connectedUsers.entrySet().stream()
                .filter(entry -> entry.getValue().equals(username))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}

