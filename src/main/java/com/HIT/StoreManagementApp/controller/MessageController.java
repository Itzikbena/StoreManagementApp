package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.MessageEntity;
import com.HIT.StoreManagementApp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Endpoint to retrieve all messages
    @GetMapping("/all")
    public List<MessageEntity> getAllMessages() {
        return messageService.getAllMessages();
    }

    // Endpoint to retrieve messages between two users
    @GetMapping("/between")
    public List<MessageEntity> getMessagesBetween(@RequestParam String senderId, @RequestParam String receiverId) {
        return messageService.getMessagesBetweenUsers(senderId, receiverId);
    }
}
