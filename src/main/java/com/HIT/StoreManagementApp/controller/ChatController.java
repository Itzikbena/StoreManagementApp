package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.ChatMessage;
import com.HIT.StoreManagementApp.model.MessageEntity;
import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.service.MessageService;
import com.HIT.StoreManagementApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;




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
    public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage) {

        // Create a MessageEntity object and save it to the database
        MessageEntity messageEntity = new MessageEntity(
                chatMessage.getUsername(),
                chatMessage.getRecipient(),
                chatMessage.getMessage()
        );
        messageEntity.setReceived(true);
        messageService.saveMessage(messageEntity);

        System.out.println("Broadcasting private message: " + chatMessage);

        // Return the ChatMessage to broadcast it to the topic
        return chatMessage;
    }

    @GetMapping("/status/{username}")
    public ResponseEntity<Map<String, Boolean>> getUserStatus(@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Map<String, Boolean> response = new HashMap<>();
            response.put("busy", user.getisbusy()); // Return the busy status of the user
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if user not found
        }
    }

    @PostMapping("/updateBusyStatus")
    public ResponseEntity<String> updateBusyStatus(@RequestBody Map<String, Object> payload) {
        String username1 = (String) payload.get("username1");
        String username2 = (String) payload.get("username2");
        boolean busy = (Boolean) payload.get("busy");

        try {
            // Update the busy status of the first user
            userService.updateUserBusyStatus(username1, busy);

            // Update the busy status of the second user
            userService.updateUserBusyStatus(username2, busy);

            return ResponseEntity.ok("Busy status updated successfully for both users");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating busy status for both users");
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
        System.out.println("Returning authenticated username: " + username);
        return response;  // Return the username as JSON
    }

    // Endpoint to retrieve missed messages for a user
    @GetMapping("/api/missedMessages")
    public ResponseEntity<List<Message>> getMissedMessages(@RequestParam String username) {
        List<Message> messages = messageService.getMissedMessages(username);
        if (messages == null || messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(messages);
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
