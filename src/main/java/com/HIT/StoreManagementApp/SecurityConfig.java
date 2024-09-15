package com.HIT.StoreManagementApp;

import com.HIT.StoreManagementApp.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/api/**"),
                                new AntPathRequestMatcher("/admin/**")
                        )
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/login").permitAll()  // Allow access to login page
                        .requestMatchers("/infopage").authenticated()  // Require authentication for infopage
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Allow only ADMIN to access admin URLs
                        .requestMatchers("/employee/**").hasAnyRole("ADMIN", "EMPLOYEE")  // Allow both ADMIN and EMPLOYEE
                        .anyRequest().authenticated()  // All other requests must be authenticated
                )
                .httpBasic(withDefaults())  // Enable Basic Authentication
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}