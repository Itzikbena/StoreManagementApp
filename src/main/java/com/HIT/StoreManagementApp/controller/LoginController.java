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
    public ResponseEntity<Map<String, Long>> login(@RequestParam String username,
                                                   @RequestParam String password,
                                                   HttpServletRequest request) {
        User user = userService.validateUser(username, password);
        if (user != null) {
            Long branchId = user.getBranch().getId();

            // Store branchId and username in session
            request.getSession().setAttribute("branchId", branchId);
            request.getSession().setAttribute("username", username);

            // Return branchId in the response JSON
            Map<String, Long> response = new HashMap<>();
            response.put("branchId", branchId);
            return ResponseEntity.ok(response);  // Return the branchId as JSON
        } else {
            // If invalid credentials, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
