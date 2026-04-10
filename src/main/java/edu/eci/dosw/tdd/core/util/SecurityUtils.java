package edu.eci.dosw.tdd.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityUtils")
public class SecurityUtils {

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? (String) auth.getPrincipal() : null;
    }

    public String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? (String) auth.getCredentials() : null;
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public boolean isLibrarian() {
        return hasRole("LIBRARIAN");
    }

    public boolean isOwner(String userId) {
        return userId != null && userId.equals(getCurrentUserId());
    }
}
