package com.HIT.StoreManagementApp.repository;

import com.HIT.StoreManagementApp.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findBySenderIdOrReceiverId(String senderId, String receiverId);


}
