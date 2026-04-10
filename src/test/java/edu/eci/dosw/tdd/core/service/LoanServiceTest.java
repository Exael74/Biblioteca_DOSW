package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.persistence.BookRepository;
import edu.eci.dosw.tdd.persistence.LoanRepository;
import edu.eci.dosw.tdd.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    private User testUser;
    private Book testBook;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-001");
        testUser.setName("Juan Pérez");
        testUser.setUsername("juan");
        testUser.setRole(Role.USER);

        testBook = new Book();
        testBook.setId("book-001");
        testBook.setTitle("Clean Code");
        testBook.setAuthor("Robert C. Martin");
        testBook.setTotalStock(5);
        testBook.setAvailableCopies(5);

        testLoan = new Loan();
        testLoan.setId("loan-001");
        testLoan.setBook(testBook);
        testLoan.setUser(testUser);
        testLoan.setLoanDate(LocalDate.now());
        testLoan.setStatus(LoanStatus.ACTIVE);
        testLoan.setHistory(new ArrayList<>());
    }

    // --- createLoan ---

    @Test
    @DisplayName("Crear préstamo exitosamente")
    void createLoan_success() {
        when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = loanService.createLoan("book-001", "user-001");

        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        assertEquals(LocalDate.now(), result.getLoanDate());
        assertNotNull(result.getHistory());
        assertFalse(result.getHistory().isEmpty());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Crear préstamo con usuario inexistente lanza excepción")
    void createLoan_userNotFound_throwsException() {
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> loanService.createLoan("book-001", "user-999"));
    }

    @Test
    @DisplayName("Crear préstamo con libro inexistente lanza excepción")
    void createLoan_bookNotFound_throwsException() {
        when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById("book-999")).thenReturn(Optional.empty());

        assertThrows(BookNotAvailableException.class, () -> loanService.createLoan("book-999", "user-001"));
    }

    @Test
    @DisplayName("Crear préstamo sin copias disponibles lanza excepción")
    void createLoan_noCopies_throwsException() {
        testBook.setAvailableCopies(0);
        when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));

        assertThrows(BookNotAvailableException.class, () -> loanService.createLoan("book-001", "user-001"));
    }

    // --- returnLoan ---

    @Test
    @DisplayName("Devolver préstamo exitosamente")
    void returnLoan_success() {
        testBook.setAvailableCopies(4);
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = loanService.returnLoan("loan-001");

        assertEquals(LoanStatus.RETURNED, result.getStatus());
        assertEquals(LocalDate.now(), result.getReturnDate());
    }

    @Test
    @DisplayName("Devolver préstamo ya devuelto lanza excepción")
    void returnLoan_alreadyReturned_throwsException() {
        testLoan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));

        assertThrows(IllegalStateException.class, () -> loanService.returnLoan("loan-001"));
    }

    @Test
    @DisplayName("Devolver préstamo inexistente lanza excepción")
    void returnLoan_notFound_throwsException() {
        when(loanRepository.findById("loan-999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> loanService.returnLoan("loan-999"));
    }

    @Test
    @DisplayName("Devolver préstamo cuando copias ya igualan stock total")
    void returnLoan_copiesEqualStock_doesNotExceed() {
        testBook.setAvailableCopies(5); // ya al máximo
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = loanService.returnLoan("loan-001");

        assertEquals(LoanStatus.RETURNED, result.getStatus());
        // No se llama bookRepository.save porque copias >= totalStock
        verify(bookRepository, never()).save(any(Book.class));
    }

    // --- deleteLoan ---

    @Test
    @DisplayName("Eliminar préstamo devuelto exitosamente")
    void deleteLoan_success() {
        testLoan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));

        assertDoesNotThrow(() -> loanService.deleteLoan("loan-001"));
        verify(loanRepository).deleteById("loan-001");
    }

    @Test
    @DisplayName("Eliminar préstamo activo lanza excepción")
    void deleteLoan_active_throwsException() {
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));

        assertThrows(IllegalStateException.class, () -> loanService.deleteLoan("loan-001"));
    }

    @Test
    @DisplayName("Eliminar préstamo inexistente lanza excepción")
    void deleteLoan_notFound_throwsException() {
        when(loanRepository.findById("loan-999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> loanService.deleteLoan("loan-999"));
    }

    // --- validateLoanOwnership ---

    @Test
    @DisplayName("Validar ownership como bibliotecario pasa siempre")
    void validateOwnership_librarian_passes() {
        assertDoesNotThrow(() -> loanService.validateLoanOwnership("loan-001", "any-user", true));
    }

    @Test
    @DisplayName("Validar ownership como dueño del préstamo pasa")
    void validateOwnership_owner_passes() {
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));

        assertDoesNotThrow(() -> loanService.validateLoanOwnership("loan-001", "user-001", false));
    }

    @Test
    @DisplayName("Validar ownership como otro usuario lanza excepción")
    void validateOwnership_notOwner_throwsException() {
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));

        assertThrows(AccessDeniedException.class,
                () -> loanService.validateLoanOwnership("loan-001", "user-other", false));
    }

    @Test
    @DisplayName("Validar ownership con préstamo inexistente lanza excepción")
    void validateOwnership_notFound_throwsException() {
        when(loanRepository.findById("loan-999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> loanService.validateLoanOwnership("loan-999", "user-001", false));
    }

    // --- getAllLoans ---

    @Test
    @DisplayName("Obtener todos los préstamos")
    void getAllLoans_returnsList() {
        when(loanRepository.findAll()).thenReturn(List.of(testLoan));

        List<Loan> result = loanService.getAllLoans();

        assertEquals(1, result.size());
        assertEquals("loan-001", result.get(0).getId());
    }

    @Test
    @DisplayName("Obtener todos los préstamos vacío")
    void getAllLoans_returnsEmptyList() {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<Loan> result = loanService.getAllLoans();

        assertTrue(result.isEmpty());
    }

    // --- getLoansByUserId ---

    @Test
    @DisplayName("Obtener préstamos por usuario retorna lista")
    void getLoansByUserId_returnsList() {
        when(loanRepository.findByUserId("user-001")).thenReturn(List.of(testLoan));

        List<Loan> result = loanService.getLoansByUserId("user-001");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Obtener préstamos por usuario sin resultados")
    void getLoansByUserId_returnsEmpty() {
        when(loanRepository.findByUserId("user-999")).thenReturn(Collections.emptyList());

        List<Loan> result = loanService.getLoansByUserId("user-999");

        assertTrue(result.isEmpty());
    }

    // --- Tests del reto 6 (mantener originales) ---

    @Test
    @DisplayName("Dado 1 reserva registrada, consulta exitosa validando id")
    void givenOneLoan_whenFindAll_thenReturnsLoanWithCorrectId() {
        when(loanRepository.findAll()).thenReturn(List.of(testLoan));

        List<Loan> loans = loanService.getAllLoans();

        assertFalse(loans.isEmpty());
        assertEquals("loan-001", loans.get(0).getId());
    }

    @Test
    @DisplayName("Sin reservas, consulta no retorna resultados")
    void givenNoLoans_whenFindAll_thenReturnsEmptyList() {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<Loan> loans = loanService.getAllLoans();

        assertTrue(loans.isEmpty());
    }

    @Test
    @DisplayName("Sin reservas, creación exitosa")
    void givenNoLoans_whenCreateLoan_thenCreationIsSuccessful() {
        when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan created = loanService.createLoan("book-001", "user-001");

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(LoanStatus.ACTIVE, created.getStatus());
    }

    @Test
    @DisplayName("Con 1 reserva, eliminación exitosa")
    void givenOneLoan_whenDelete_thenDeletionIsSuccessful() {
        testLoan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));

        assertDoesNotThrow(() -> loanService.deleteLoan("loan-001"));
        verify(loanRepository).deleteById("loan-001");
    }

    @Test
    @DisplayName("Con 1 reserva, eliminar y consultar retorna vacío")
    void givenOneLoan_whenDeleteAndFindAll_thenReturnsEmpty() {
        testLoan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById("loan-001")).thenReturn(Optional.of(testLoan));

        loanService.deleteLoan("loan-001");

        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<Loan> loans = loanService.getAllLoans();
        assertTrue(loans.isEmpty());
    }
}
