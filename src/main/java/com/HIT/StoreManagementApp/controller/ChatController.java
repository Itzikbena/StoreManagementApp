package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.ChatMessage;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> activeUsers = new HashSet<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/userLogin")
    @SendTo("/topic/activeUsers")
    public List<String> handleUserLogin(ChatMessage message) {
        activeUsers.add(message.getUsername());  // Add user to active users set
        return new ArrayList<>(activeUsers);     // Return list of active users
    }

    @MessageMapping("/userLogout")
    @SendTo("/topic/activeUsers")
    public List<String> handleUserLogout(ChatMessage message) {
        activeUsers.remove(message.getUsername());  // Remove user from active users set
        return new ArrayList<>(activeUsers);        // Return updated list of active users
    }

    @MessageMapping("/sendToUser")
    @SendTo("/topic/messages")
    public ChatMessage sendMessageToUser(ChatMessage message) {
        return message;  // Broadcast public message to everyone
    }

    @MessageMapping("/sendPrivateMessage")
    public void sendPrivateMessage(ChatMessage message) {
        String recipient = message.getRecipient();
        if (recipient != null && !recipient.isEmpty()) {
            System.out.println("Sending private message to: " + recipient);
            messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", message);
        } else {
            System.out.println("Recipient is null or empty.");
        }
    }

    @GetMapping("/api/getUsername")
    @ResponseBody
    public Map<String, String> getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Get the authenticated username

        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        return response;  // Return the username as JSON
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        // Initialize active user tracking here if needed
    }
}

