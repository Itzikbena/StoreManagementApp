package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.ChatMessage;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> activeUsers = new HashSet<>();
    private final Map<String, List<ChatMessage>> missedMessages = new ConcurrentHashMap<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Handle user login
    @MessageMapping("/userLogin")
    @SendTo("/topic/activeUsers")
    public List<String> handleUserLogin(ChatMessage message) {
        activeUsers.add(message.getUsername());  // Add user to active users set

        // Send missed messages if any exist for this user
        if (missedMessages.containsKey(message.getUsername())) {
            List<ChatMessage> messages = missedMessages.remove(message.getUsername());
            messages.forEach(m -> messagingTemplate.convertAndSendToUser(message.getUsername(), "/queue/messages", m));
        }

        return new ArrayList<>(activeUsers);     // Return list of active users
    }

    // Handle user logout
    @MessageMapping("/userLogout")
    @SendTo("/topic/activeUsers")
    public List<String> handleUserLogout(ChatMessage message) {
        activeUsers.remove(message.getUsername());  // Remove user from active users set
        return new ArrayList<>(activeUsers);        // Return updated list of active users
    }

    // Handle public message
    @MessageMapping("/sendToUser")
    @SendTo("/topic/messages")
    public ChatMessage sendMessageToUser(ChatMessage message) {
        return message;  // Broadcast public message to everyone
    }

    // Handle private message
    @MessageMapping("/sendPrivateMessage")
    public void sendPrivateMessage(ChatMessage message) {
        String recipient = message.getRecipient();
        if (recipient != null && !recipient.isEmpty()) {
            System.out.println("Sending private message to: " + recipient);
            if (activeUsers.contains(recipient)) {
                // Send message if recipient is active
                messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", message);
            } else {
                // Store missed message if recipient is not active
                missedMessages.computeIfAbsent(recipient, k -> new ArrayList<>()).add(message);
                System.out.println("Stored missed message for: " + recipient);
            }
        } else {
            System.out.println("Recipient is null or empty.");
        }
    }

    // Get the authenticated username
    @GetMapping("/api/getUsername")
    @ResponseBody
    public Map<String, String> getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Get the authenticated username

        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        return response;  // Return the username as JSON
    }

    // Endpoint to retrieve missed messages for a user
    @GetMapping("/api/missedMessages")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getMissedMessages(@RequestParam String username) {
        List<ChatMessage> messages = missedMessages.getOrDefault(username, new ArrayList<>());
        missedMessages.remove(username);  // Clear missed messages after sending
        return ResponseEntity.ok(messages);
    }

    // Initialize active user tracking if needed
    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        // Add initialization logic here if necessary
    }
}
