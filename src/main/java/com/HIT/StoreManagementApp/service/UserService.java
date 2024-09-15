package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;  // Correct import
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public User validateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Check if the password matches
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;  // Return null if the user does not exist or the password does not match
    }


    // Method to find a User by their ID
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null); // Return the user if found, otherwise return null
    }

    // Create an employee
    public User createEmployee(User user) {
        // Add logic for creating a new employee (e.g., encrypting the password)
        return userRepository.save(user);
    }

    // Get all employees
    public List<User> getAllEmployees() {
        return userRepository.findAll();  // Fetch all users from the database
    }

    // Delete an employee by ID
    public void deleteEmployee(Long id) {
        userRepository.deleteById(id);  // Delete user by ID
    }

    // Method to create a new user and save it to the database
    public User createUser(User user) throws Exception {
        // Check if a user with the same username already exists
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new Exception("Username already exists in the system.");  // Throw an exception if the username exists
        }

        // Encrypt the user's password before saving it
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        return userRepository.save(user);
    }
}
