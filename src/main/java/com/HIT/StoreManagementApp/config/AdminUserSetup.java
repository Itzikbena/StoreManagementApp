package com.HIT.StoreManagementApp.config;

import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminUserSetup implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if the admin user exists
        Optional<User> adminUserOptional = userRepository.findByUsername("admin");

        if (adminUserOptional.isPresent()) {
            // Update the admin password if the user exists
            User adminUser = adminUserOptional.get();
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(adminUser);
            System.out.println("Admin password updated.");
        } else {
            // If the admin user doesn't exist, create a new admin user
            User newAdmin = new User();
            newAdmin.setUsername("admin");
            newAdmin.setPassword(passwordEncoder.encode("admin123"));
            newAdmin.setRole("ROLE_ADMIN");
            userRepository.save(newAdmin);
            System.out.println("Admin user created with default password.");
        }
    }
}
