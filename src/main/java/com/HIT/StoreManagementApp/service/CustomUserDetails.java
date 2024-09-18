package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public Long getBranchId() {
        return user.getBranch() != null ? user.getBranch().getId() : null;  // Ensure branch exists
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert the user's role into a GrantedAuthority object
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
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
        return true;  // Customize as needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Customize as needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Customize as needed
    }

    @Override
    public boolean isEnabled() {
        return true;  // Customize as needed
    }
}
