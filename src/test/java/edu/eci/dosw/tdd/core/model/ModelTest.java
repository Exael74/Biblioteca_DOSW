package edu.eci.dosw.tdd.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    // --- Book ---

    @Test
    @DisplayName("Book isAvailable retorna true cuando hay copias")
    void book_isAvailable_true() {
        Book book = new Book();
        book.setAvailableCopies(3);
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("Book isAvailable retorna false cuando no hay copias")
    void book_isAvailable_false() {
        Book book = new Book();
        book.setAvailableCopies(0);
        assertFalse(book.isAvailable());
    }

    @Test
    @DisplayName("Book getBorrowedCopies calcula correctamente")
    void book_getBorrowedCopies() {
        Book book = new Book();
        book.setTotalStock(10);
        book.setAvailableCopies(7);
        assertEquals(3, book.getBorrowedCopies());
    }

    @Test
    @DisplayName("Book campos extendidos")
    void book_extendedFields() {
        Book book = new Book();
        book.setIsbn("978-123");
        book.setCategories(List.of("Ficción", "Drama"));
        book.setPublicationType("LIBRO");
        book.setPublicationDate(LocalDate.of(2020, 1, 1));
        book.setPages(300);
        book.setLanguage("Español");
        book.setPublisher("Editorial");
        book.setAddedToCatalogDate(LocalDate.now());

        assertEquals("978-123", book.getIsbn());
        assertEquals(2, book.getCategories().size());
        assertEquals("LIBRO", book.getPublicationType());
        assertEquals(300, book.getPages());
        assertEquals("Español", book.getLanguage());
        assertEquals("Editorial", book.getPublisher());
        assertNotNull(book.getAddedToCatalogDate());
    }

    // --- User ---

    @Test
    @DisplayName("User campos básicos y extendidos")
    void user_allFields() {
        User user = new User();
        user.setId("u1");
        user.setName("Test");
        user.setUsername("test");
        user.setPassword("pass");
        user.setRole(Role.USER);
        user.setEmail("test@mail.com");
        user.setMembershipType("VIP");
        user.setRegistrationDate(LocalDate.now());

        assertEquals("u1", user.getId());
        assertEquals("Test", user.getName());
        assertEquals("test", user.getUsername());
        assertEquals(Role.USER, user.getRole());
        assertEquals("test@mail.com", user.getEmail());
        assertEquals("VIP", user.getMembershipType());
        assertNotNull(user.getRegistrationDate());
    }

    // --- Loan ---

    @Test
    @DisplayName("Loan con historial")
    void loan_withHistory() {
        Loan loan = new Loan();
        loan.setId("l1");
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLoanDate(LocalDate.now());

        List<Loan.LoanHistoryEntry> history = new ArrayList<>();
        history.add(new Loan.LoanHistoryEntry("ACTIVE", LocalDate.now()));
        loan.setHistory(history);

        assertEquals(1, loan.getHistory().size());
        assertEquals("ACTIVE", loan.getHistory().get(0).getStatus());
    }

    @Test
    @DisplayName("Loan historial vacío por defecto")
    void loan_defaultHistory() {
        Loan loan = new Loan();
        assertNotNull(loan.getHistory());
        assertTrue(loan.getHistory().isEmpty());
    }

    // --- LoanStatus ---

    @Test
    @DisplayName("LoanStatus valores")
    void loanStatus_values() {
        assertEquals(2, LoanStatus.values().length);
        assertEquals(LoanStatus.ACTIVE, LoanStatus.valueOf("ACTIVE"));
        assertEquals(LoanStatus.RETURNED, LoanStatus.valueOf("RETURNED"));
    }

    // --- Role ---

    @Test
    @DisplayName("Role valores")
    void role_values() {
        assertEquals(2, Role.values().length);
        assertEquals(Role.USER, Role.valueOf("USER"));
        assertEquals(Role.LIBRARIAN, Role.valueOf("LIBRARIAN"));
    }
}
