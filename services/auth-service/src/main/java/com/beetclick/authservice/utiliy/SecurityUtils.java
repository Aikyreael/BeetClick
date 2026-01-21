package com.beetclick.authservice.utiliy;

import com.beetclick.authservice.entity.AuthUser;
import com.beetclick.authservice.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthUser getCurrentUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof AuthUser)) {
            throw new UnauthorizedException("Utilisateur non authentifié");
        }
        return (AuthUser) authentication.getPrincipal();
    }

    public static AuthUser getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof AuthUser)) {
            return null;
        }
        return (AuthUser) authentication.getPrincipal();
    }

}