package com.HIT.StoreManagementApp;

import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.model.User;
import com.HIT.StoreManagementApp.service.CustomUserDetails;
import com.HIT.StoreManagementApp.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Inject the CustomUserDetailsService
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService; // Injecting CustomUserDetailsService
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/api/**"),
                                new AntPathRequestMatcher("/admin/**"),
                                new AntPathRequestMatcher("/chat/**")
                        )
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/alreadyOnline").permitAll()
                        .requestMatchers("/chat-websocket/**").permitAll()
                        .requestMatchers("/admin/users/isoffline").permitAll() // Allow everyone to access isoffline
                        .requestMatchers("/admin/users/isonline").permitAll()
                        .requestMatchers("/admin/users/**").permitAll()
                        .requestMatchers("/api/customers").permitAll()
                        .requestMatchers("/admin/sales/**").permitAll()
                        .requestMatchers("/admin/customers/**").permitAll()
                        .requestMatchers("/admin/users/list").permitAll() // Allow everyone to access isonline
                        .requestMatchers("/login").permitAll()  // Allow access to the login page and custom POST login
                        .requestMatchers("/infopage").authenticated()  // Protect the infopage
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Admin access
                        .requestMatchers("/employee/**").hasAnyRole("ADMIN", "EMPLOYEE")  // Employee access
                        .requestMatchers("/chat/**").permitAll()  // Open chat to everyone
                        .anyRequest().authenticated()  // Everything else requires authentication
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(formLogin -> formLogin
                        .successHandler(successHandler())  // Use custom success handler for additional logic
                )
                .httpBasic(withDefaults())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider())  // Use custom authentication provider
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .headers(headers -> headers
                        .addHeaderWriter((request, response) -> {
                            response.setHeader("X-Frame-Options", "ALLOWALL");  // Allow iframes
                        })
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        return (request, response, authentication) -> {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            String nameuser = userDetails.getUsername(); // Get the actual User entity
            Long userId = user.getId();
            Branch branch = user.getBranch();  // Access the branch via User entity

            boolean online = user.isOnline();


            if (online) {
                // Log online status
                logger.info("User {} is online: {}", userId, online);
                // If user is online, redirect to alreadyOnline page with userId as a query parameter
                response.sendRedirect("/alreadyOnline?userId=" + userId + "&userName=" + nameuser);
            }
             else {
                // Check if the user has the role 'ROLE_ADMIN'
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

                if (isAdmin) {
                    // Log admin access
                    logger.info("Admin access granted for user ID: {}. Redirecting to /ADMIN.", userId);
                    response.sendRedirect("/ADMIN");
                } else if (branch != null) {
                    // Log non-admin access
                    logger.info("Non-admin user ID: {}. Redirecting to /infopage with branchId: {} and userId: {}", userId, branch.getId(), userId);
                    response.sendRedirect("/infopage?branchId=" + branch.getId() + "&userId=" + userId);
                } else {
                    // If no branch, handle it gracefully
                    logger.warn("Non-admin user ID: {} does not have a branch assigned. Redirecting to default page.", userId);
                    response.sendRedirect("/default"); // Redirect to a default page or show an error
                }
            }
        };
    }


}


