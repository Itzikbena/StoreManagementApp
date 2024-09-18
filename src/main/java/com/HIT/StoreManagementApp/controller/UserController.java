package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.service.BranchService;
import com.HIT.StoreManagementApp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    // Fetch the list of all users
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();  // Fetch the list of users
        return ResponseEntity.ok(users);
    }

    // Fetch details of a specific chat user by userId
    @GetMapping("/currentChatUser")
    public ResponseEntity<User> getCurrentChatUser(@RequestParam Long userId) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new user and assign them to a branch
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, @RequestParam Long branchId) {
        Branch branch = branchService.getBranchById(branchId);
        if (branch == null) {
            return ResponseEntity.badRequest().body("Branch not found.");
        }

        // Assign the branch to the user
        user.setBranch(branch);

        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update user's online status to true (when they log in)
    @PostMapping("/isonline")
    public ResponseEntity<?> markUserOnline(@RequestParam Long userId) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setOnline(true);
            userService.updateUser(user);
            return ResponseEntity.ok("User is now online.");
        } else {
            return ResponseEntity.badRequest().body("User not found.");
        }
    }

    // Update user's online status to false (when they log out)
    @PostMapping("/isoffline")
    public ResponseEntity<?> markUserOffline(@RequestParam Long userId) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setOnline(false);
            userService.updateUser(user);
            return ResponseEntity.ok("User is now offline.");
        } else {
            return ResponseEntity.badRequest().body("User not found.");
        }
    }

    // NEW: Get the branchId and userId of the currently authenticated user
    @GetMapping("/getUserInfo")
    public ResponseEntity<?> getCurrentUserInfo() {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // Get the username from the security context

            // Find the user by username
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Long branchId = user.getBranch().getId();  // Get user's branchId
                Long userId = user.getId();  // Get user's userId

                // Prepare the response data
                Map<String, Object> response = new HashMap<>();
                response.put("branchId", branchId);
                response.put("userId", userId);

                // Return the response as JSON
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.badRequest().body("User not found or not authenticated.");
    }
}
