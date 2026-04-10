package edu.eci.dosw.tdd.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    // --- ApiMessages ---

    @Test
    @DisplayName("ApiMessages constantes no son null")
    void apiMessages_constantsNotNull() {
        assertNotNull(ApiMessages.BOOK_NOT_FOUND);
        assertNotNull(ApiMessages.BOOK_NOT_AVAILABLE);
        assertNotNull(ApiMessages.BOOK_ALREADY_EXISTS);
        assertNotNull(ApiMessages.BOOK_INVALID_STOCK);
        assertNotNull(ApiMessages.BOOK_INVALID_COPIES);
        assertNotNull(ApiMessages.BOOK_RETURN_EXCEEDS_STOCK);
        assertNotNull(ApiMessages.BOOK_HAS_ACTIVE_LOANS);
        assertNotNull(ApiMessages.USER_NOT_FOUND);
        assertNotNull(ApiMessages.USER_ALREADY_EXISTS);
        assertNotNull(ApiMessages.USER_HAS_ACTIVE_LOANS);
        assertNotNull(ApiMessages.INVALID_CREDENTIALS);
        assertNotNull(ApiMessages.USERNAME_ALREADY_EXISTS);
        assertNotNull(ApiMessages.LOAN_NOT_FOUND);
        assertNotNull(ApiMessages.LOAN_ALREADY_RETURNED);
        assertNotNull(ApiMessages.LOAN_CANNOT_DELETE_ACTIVE);
    }

    // --- SecurityUtils ---

    @Test
    @DisplayName("SecurityUtils getCurrentUsername con contexto")
    void securityUtils_getCurrentUsername() {
        SecurityUtils utils = new SecurityUtils();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "juan", "user-001", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("juan", utils.getCurrentUsername());
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityUtils getCurrentUserId con contexto")
    void securityUtils_getCurrentUserId() {
        SecurityUtils utils = new SecurityUtils();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "juan", "user-001", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("user-001", utils.getCurrentUserId());
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityUtils hasRole retorna true para rol correcto")
    void securityUtils_hasRole_true() {
        SecurityUtils utils = new SecurityUtils();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "juan", "user-001", List.of(new SimpleGrantedAuthority("ROLE_LIBRARIAN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(utils.hasRole("LIBRARIAN"));
        assertFalse(utils.hasRole("USER"));
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityUtils isLibrarian")
    void securityUtils_isLibrarian() {
        SecurityUtils utils = new SecurityUtils();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin", "admin-001", List.of(new SimpleGrantedAuthority("ROLE_LIBRARIAN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(utils.isLibrarian());
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityUtils isOwner retorna true para el mismo user")
    void securityUtils_isOwner_true() {
        SecurityUtils utils = new SecurityUtils();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "juan", "user-001", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(utils.isOwner("user-001"));
        assertFalse(utils.isOwner("user-002"));
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityUtils isOwner con null retorna false")
    void securityUtils_isOwner_null() {
        SecurityUtils utils = new SecurityUtils();
        assertFalse(utils.isOwner(null));
    }

    @Test
    @DisplayName("SecurityUtils sin contexto retorna null")
    void securityUtils_noContext_returnsNull() {
        SecurityUtils utils = new SecurityUtils();
        SecurityContextHolder.clearContext();

        assertNull(utils.getCurrentUsername());
        assertNull(utils.getCurrentUserId());
        assertFalse(utils.hasRole("USER"));
    }
}
