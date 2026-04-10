package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.LoanRepository;
import edu.eci.dosw.tdd.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-001");
        testUser.setName("Juan Pérez");
        testUser.setUsername("juan");
        testUser.setPassword("hashedpassword");
        testUser.setRole(Role.USER);
    }

    // --- getAllUsers ---

    @Test
    @DisplayName("Obtener todos los usuarios retorna lista")
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("user-001", result.get(0).getId());
    }

    @Test
    @DisplayName("Obtener todos los usuarios retorna lista vacía")
    void getAllUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    // --- getUserById ---

    @Test
    @DisplayName("Obtener usuario por ID exitosamente")
    void getUserById_success() {
        when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

        User result = userService.getUserById("user-001");

        assertEquals("user-001", result.getId());
        assertEquals("Juan Pérez", result.getName());
    }

    @Test
    @DisplayName("Obtener usuario por ID inexistente lanza excepción")
    void getUserById_notFound_throwsException() {
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("user-999"));
    }

    // --- deleteUser ---

    @Test
    @DisplayName("Eliminar usuario exitosamente")
    void deleteUser_success() {
        when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
        when(loanRepository.existsByUserIdAndStatus("user-001", "ACTIVE")).thenReturn(false);

        assertDoesNotThrow(() -> userService.deleteUser("user-001"));
        verify(loanRepository).deleteByUserId("user-001");
        verify(userRepository).deleteById("user-001");
    }

    @Test
    @DisplayName("Eliminar usuario con préstamos activos lanza excepción")
    void deleteUser_activeLoans_throwsException() {
        when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
        when(loanRepository.existsByUserIdAndStatus("user-001", "ACTIVE")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.deleteUser("user-001"));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar usuario inexistente lanza excepción")
    void deleteUser_notFound_throwsException() {
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("user-999"));
    }
}
