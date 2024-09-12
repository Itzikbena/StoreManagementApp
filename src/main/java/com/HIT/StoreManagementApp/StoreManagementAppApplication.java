package com.HIT.StoreManagementApp;

import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class StoreManagementAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreManagementAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin") == null) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole("ADMIN");
				// Add branch if applicable
				userRepository.save(admin);
				System.out.println("Admin user created: admin/admin123");
			}
		};
	}
}
