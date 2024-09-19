package com.HIT.StoreManagementApp.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {

    // This map simulates the storage of missed messages
    private Map<String, List<String>> missedMessagesStore = new ConcurrentHashMap<>();

    // Retrieve missed messages for a specific user
    public List<String> getMissedMessagesForUser(String username) {
        // Fetch and return missed messages from the store
        return missedMessagesStore.getOrDefault(username, new ArrayList<>());
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
