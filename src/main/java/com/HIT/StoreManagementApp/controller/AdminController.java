package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/employees")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createEmployee(@RequestBody User user) {
        return userService.createEmployee(user);
    }

    @GetMapping
    public List<User> getAllEmployees() {
        return userService.getAllEmployees();
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        userService.deleteEmployee(id);
    }
}
