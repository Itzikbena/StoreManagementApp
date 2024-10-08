package com.HIT.StoreManagementApp.controller;

import ch.qos.logback.core.model.Model;
import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;  // Injecting UserService


    @GetMapping("/ADMIN")
    public String adminPage() {
        return "admin"; // This assumes admin.html is in `src/main/resources/templates`
    }

    @GetMapping("/alreadyOnline")
    public ModelAndView alreadyOnlinePage(@RequestParam("userId") Long userId,
                                          @RequestParam("userName") String userName) {
        ModelAndView modelAndView = new ModelAndView("alreadyOnline"); // Thymeleaf template name
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("userName", userName);
        return modelAndView;
    }


    @GetMapping("/login")
    public String login() {
        return "login";  // This will map to login.html in the templates folder
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username,
                                                     @RequestParam String password,
                                                     HttpServletRequest request) {
        User user = userService.validateUser(username, password);
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            Long branchId = user.getBranch().getId();
            Long userId = user.getId();

            // Authenticate the user manually
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Set session attributes
            request.getSession().setAttribute("branchId", branchId);
            request.getSession().setAttribute("username", username);
            request.getSession().setAttribute("userId", userId);

            response.put("branchId", branchId);
            response.put("userId", userId);

            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(response);
        }
    }


}
