package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final String role;  // Role with correct prefix

    public CustomUserDetails(User user, String role) {
        this.user = user;
        this.role = role;  // Store the role with the "ROLE_" prefix
    }

    public Long getId() {
        return user.getId();
    }

    // Expose user entity via getter
    public User getUser() {
        return user;
    }

    // You can also provide direct access to the branch if needed
    public Branch getBranch() {
        return user.getBranch();  // Assuming User entity has a getBranch method
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return the role as a GrantedAuthority
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
