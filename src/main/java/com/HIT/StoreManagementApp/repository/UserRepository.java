package com.HIT.StoreManagementApp.repository;

import com.HIT.StoreManagementApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // Method to find user by username

    @Modifying
    @Query("UPDATE User u SET u.online = :online WHERE u.id = :userId")
    void updateOnlineStatus(Long userId, boolean online);

}
