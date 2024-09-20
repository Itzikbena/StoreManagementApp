package com.HIT.StoreManagementApp.service;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {

    // This map simulates the storage of missed messages
    private Map<String, List<String>> missedMessagesStore = new ConcurrentHashMap<>();

    private Map<String, List<Message>> missedMessages = new HashMap<>();


    // Retrieve missed messages for a specific user
    public List<Message> getMissedMessages(String username) {
        List<Message> messages = missedMessages.getOrDefault(username, new ArrayList<>());
        // Clear messages after retrieving them
        missedMessages.remove(username);
        return messages;
    }
    public void addMissedMessage(String recipient, Message message) {
        missedMessages.computeIfAbsent(recipient, k -> new ArrayList<>()).add(message);
    }

    // Save missed messages for a user (this method should be called when the user is offline)
    public void saveMissedMessageForUser(String username, String message) {
        missedMessagesStore.computeIfAbsent(username, k -> new ArrayList<>()).add(message);
    }

    // Remove missed messages for a user after they have been retrieved
    public void clearMissedMessagesForUser(String username) {
        missedMessagesStore.remove(username);
    }
}
