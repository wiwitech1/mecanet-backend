package com.wiwitech.mecanetbackend.shared.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.wiwitech.mecanetbackend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return null;
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }
} 