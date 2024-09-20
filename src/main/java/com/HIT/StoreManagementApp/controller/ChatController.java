package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.ChatMessage;
import com.HIT.StoreManagementApp.service.MessageService;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>()); // Synchronized set for thread-safety
    private final Map<String, List<ChatMessage>> missedMessages = new ConcurrentHashMap<>();

    private final List<ChatMessage> messageLog = new ArrayList<>();

    private MessageService messageService; // A service to handle message logic



    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Handle user login
    @MessageMapping("/userLogin")
    public void handleUserLogin(ChatMessage message) {
        String username = message.getUsername();
        if (username != null && !username.isEmpty()) {
            synchronized (activeUsers) {
                activeUsers.add(username);  // Add user to active users set
            }
            System.out.println("User logged in: " + username);

            // Send missed messages if any exist for this user
            if (missedMessages.containsKey(username)) {
                List<ChatMessage> messages = missedMessages.remove(username); // Remove messages after retrieving
                messages.forEach(m -> messagingTemplate.convertAndSendToUser(username, "/queue/messages", m));
                System.out.println("Sent missed messages to user: " + username);
            }

            // Send updated active user list to all subscribers
            messagingTemplate.convertAndSend("/topic/activeUsers", new ArrayList<>(activeUsers));
        } else {
            System.err.println("Username is null or empty in userLogin message.");
        }
    }

    // Handle user logout
    @MessageMapping("/userLogout")
    public void handleUserLogout(ChatMessage message) {
        String username = message.getUsername();
        if (username != null && !username.isEmpty()) {
            synchronized (activeUsers) {
                activeUsers.remove(username);  // Remove user from active users set
            }
            System.out.println("User logged out: " + username);

            // Send updated active user list to all subscribers
            messagingTemplate.convertAndSend("/topic/activeUsers", new ArrayList<>(activeUsers));
        } else {
            System.err.println("Username is null or empty in userLogout message.");
        }
    }

    // Handle public message
    @MessageMapping("/sendToUser")
    @SendTo("/topic/messages")
    public ChatMessage sendMessageToUser(ChatMessage message) {
        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            System.out.println("Public message from " + message.getUsername() + ": " + message.getMessage());
            return message; // Broadcast public message to everyone
        } else {
            System.err.println("Public message is null or empty.");
            return null; // Return null to avoid sending empty messages
        }
    }

    // Handle private message
    @MessageMapping("/sendPrivateMessage")
    @SendTo("/topic/private-messages")
    public ChatMessage sendPrivateMessage(ChatMessage message) {
        System.out.println("Broadcasting private message: " + message);
        return message;  // Broadcast private message to a topic
    }

    // Get the authenticated username
    @GetMapping("/api/getUsername")
    @ResponseBody
    public Map<String, String> getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Get the authenticated username

        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        System.out.println("Returning authenticated username: " + username);
        return response;  // Return the username as JSON
    }

    // Endpoint to retrieve missed messages for a user
    @GetMapping("/api/missedMessages")
    public List<Message> getMissedMessages(@RequestParam String username) {
        return messageService.getMissedMessages(username);
    }

    // Initialize active user tracking if needed
    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        // Add initialization logic here if necessary
        System.out.println("ChatController initialized.");
    }

    @GetMapping("/api/loggedMessages")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getLoggedMessages() {
        return ResponseEntity.ok(messageLog);
    }
}
