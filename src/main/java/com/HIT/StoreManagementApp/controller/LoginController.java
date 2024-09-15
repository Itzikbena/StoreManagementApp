package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;  // Correct Model class from Spring framework
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;  // Injecting UserService

    @GetMapping("/login")
    public String login() {
        return "login";  // This will map to login.html in the templates folder
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        User user = userService.validateUser(username, password);
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            Long branchId = user.getBranch().getId();
            Long userId = user.getId();  // Get the user's ID (employee ID)

            // Set session attributes (if needed)
            request.getSession().setAttribute("branchId", branchId);
            request.getSession().setAttribute("username", username);
            request.getSession().setAttribute("userId", userId);  // Save user ID to session if needed

            // Add branchId and userId to the response
            response.put("branchId", branchId);
            response.put("userId", userId);

            // Return the response as JSON
            return ResponseEntity.ok(response);
        } else {
            // Add error message to the response
            response.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
