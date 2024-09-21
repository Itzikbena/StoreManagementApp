package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LogsService logsService; // Injecting LogsService to record logs

    // Update a user in the database and add a log
    public User updateUser(User user) {
        User updatedUser = userRepository.save(user);

        if(user.isOnline()) {
            logsService.addLog(
                    "עדכון עובד", // Action type
                    "פרטי העובד: " + user.getUsername() + " נכנס למערכת ", // Action description
                    user.getBranch().getId(),
                    2// Action description
            );
        }
        else
        {
            logsService.addLog(
                    "עדכון עובד", // Action type
                    "פרטי העובד: " + user.getUsername() + " יצא מהמערכת ", // Action description
                    user.getBranch().getId(),
                    2// Action description
            );
        }


        return updatedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(); // Fetch all users
    }

    // Method to find a user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username); // Returns Optional<User>
    }

    // Save a user and add a log
    public void save(User user) {
        userRepository.save(user);

        // Add a log entry after saving the user
        logsService.addLog(
                "הוספת עובד חדש", // Action type
                "העובד החדש: " + user.getName(), // Action description
                user.getBranch().getId(),
                2// Action description

        );
    }

    // Validate user credentials
    public User validateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Check if the password matches
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null; // Return null if the user does not exist or the password does not match
    }

    // Method to find a User by their ID
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null); // Return the user if found, otherwise return null
    }

    // New Method to find a User by their ID and return Optional<User>
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id); // Return an Optional<User>
    }

    // New Method to get all users
    public List<User> findAllUsers() {
        return userRepository.findAll(); // Return a list of all users
    }

    // Create an employee and add a log
    public User createEmployee(User user) {
        // Add logic for creating a new employee (e.g., encrypting the password)
        User createdUser = userRepository.save(user);

        // Add a log entry after saving the user
        logsService.addLog(
                "הוספת עובד חדש", // Action type
                "העובד החדש: " + user.getName(), // Action description
                user.getBranch().getId(),
                2// Action description

        );

        return createdUser;
    }

    // Get all employees
    public List<User> getAllEmployees() {
        return userRepository.findAll(); // Fetch all users from the database
    }

    // Delete an employee by ID and add a log
    public void deleteEmployee(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);

            // Add a log entry after deleting an employee
            logsService.addLog(
                    "מחיקת עובד", // Action type
                    "העובד החדש: " + user, // Action description
                    null,
                    2
            );
        }
    }

    // Method to create a new user and save it to the database and add a log
    public User createUser(User user) throws Exception {
        // Check if a user with the same username already exists
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new Exception("Username already exists in the system."); // Throw an exception if the username exists
        }

        // Encrypt the user's password before saving it
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Add a log entry after saving the user
        logsService.addLog(
                "הוספת עובד חדש", // Action type
                "העובד החדש: " + user.getName(), // Action description
                user.getBranch().getId(), // Action description
                2
        );

        // Save the user to the database
        User createdUser = userRepository.save(user);

        return createdUser;
    }
}