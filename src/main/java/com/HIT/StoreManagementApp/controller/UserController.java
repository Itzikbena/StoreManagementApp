package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.service.BranchService;
import com.HIT.StoreManagementApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")  // Ensure this mapping matches the URL in Postman
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, @RequestParam Long branchId) {
        // Find the branch by its ID
        Branch branch = branchService.getBranchById(branchId);
        if (branch == null) {
            return ResponseEntity.badRequest().body("Branch not found.");
        }

        // Assign the branch to the user
        user.setBranch(branch);

        try {
            // Save the user and return the result
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
