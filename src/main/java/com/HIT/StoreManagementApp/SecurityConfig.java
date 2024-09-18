package com.HIT.StoreManagementApp;

import com.HIT.StoreManagementApp.service.CustomUserDetails;
import com.HIT.StoreManagementApp.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); // Use injected customUserDetailsService
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
                                new AntPathRequestMatcher("/chat/**")  // Ignore CSRF for WebSocket endpoints
                        )
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/login").permitAll()  // Allow access to login page
                        .requestMatchers("/infopage").authenticated()  // Require authentication for infopage
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Allow only ADMIN to access admin URLs
                        .requestMatchers("/employee/**").hasAnyRole("ADMIN", "EMPLOYEE")  // Allow both ADMIN and EMPLOYEE
                        .requestMatchers("/chat/**").permitAll()  // Allow all access to WebSocket endpoints
                        .anyRequest().authenticated()  // All other requests must be authenticated
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // Specify the custom login page URL
                        .defaultSuccessUrl("/infopage", true)  // Redirect to /infopage after successful login
                        .successHandler(successHandler())  // Use custom success handler for additional logic
                        .permitAll()  // Allow access to the login page for all users
                )
                .httpBasic(withDefaults())  // Enable Basic Authentication
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Ensure session is created and maintained
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            Long branchId = user.getBranchId();
            Long userId = user.getId();

            // Redirect to /infopage with branchId and userId as query parameters
            response.sendRedirect("/infopage?branchId=" + branchId + "&userId=" + userId);
        };
    }

}
