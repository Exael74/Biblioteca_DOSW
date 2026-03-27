package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceRegistrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldAllowUserToRegisterWithCustomUsernameAndPassword() {
        User user = User.builder()
                .name("Carlos")
                .username("carlitos")
                .password("my-own-password")
                .role(User.Role.USER)
                .build();

        User saved = userService.addUser(user);

        assertEquals("carlitos", saved.getUsername());
        assertEquals("my-own-password", saved.getPassword());
    }

    @Test
    void shouldRequireUsernameAndPasswordWhenRegistering() {
        User missingCredentials = User.builder()
                .name("Lucia")
                .role(User.Role.USER)
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.addUser(missingCredentials));
    }

    @Test
    void shouldSupportLibrarianRole() {
        User librarian = User.builder()
                .name("Sara")
                .username("sara-lib")
                .password("secure")
                .role(User.Role.LIBRARIAN)
                .build();

        User saved = userService.addUser(librarian);

        assertEquals(User.Role.LIBRARIAN, saved.getRole());
    }
}
