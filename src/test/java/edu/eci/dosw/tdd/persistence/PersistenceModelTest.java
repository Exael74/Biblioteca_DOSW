package edu.eci.dosw.tdd.persistence;

import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceModelTest {

    // --- BookEntity ---
    @Test
    @DisplayName("BookEntity getters y setters")
    void bookEntity() {
        BookEntity e = new BookEntity();
        e.setId("b1");
        e.setTitle("T");
        e.setAuthor("A");
        e.setTotalStock(5);
        e.setAvailableCopies(3);
        assertEquals("b1", e.getId());
        assertEquals("T", e.getTitle());
        assertEquals("A", e.getAuthor());
        assertEquals(5, e.getTotalStock());
        assertEquals(3, e.getAvailableCopies());

        BookEntity e2 = new BookEntity("b2", "T2", "A2", 10, 7);
        assertEquals("b2", e2.getId());
    }

    // --- UserEntity ---
    @Test
    @DisplayName("UserEntity getters y setters")
    void userEntity() {
        UserEntity e = new UserEntity();
        e.setId("u1");
        e.setName("N");
        e.setUsername("un");
        e.setPassword("p");
        e.setRole(Role.USER);
        assertEquals("u1", e.getId());
        assertEquals(Role.USER, e.getRole());

        UserEntity e2 = new UserEntity("u2", "N2", "un2", "p2", Role.LIBRARIAN);
        assertEquals(Role.LIBRARIAN, e2.getRole());
    }

    // --- LoanEntity ---
    @Test
    @DisplayName("LoanEntity getters y setters")
    void loanEntity() {
        BookEntity book = new BookEntity("b1", "T", "A", 5, 3);
        UserEntity user = new UserEntity("u1", "N", "un", "p", Role.USER);
        LoanEntity e = new LoanEntity();
        e.setId("l1");
        e.setBook(book);
        e.setUser(user);
        e.setLoanDate(LocalDate.now());
        e.setStatus(LoanStatus.ACTIVE);
        e.setReturnDate(null);
        assertEquals("l1", e.getId());
        assertEquals("b1", e.getBook().getId());
        assertNull(e.getReturnDate());

        LoanEntity e2 = new LoanEntity("l2", book, user, LocalDate.now(), LoanStatus.RETURNED, LocalDate.now());
        assertEquals(LoanStatus.RETURNED, e2.getStatus());
    }

    // --- BookDocument ---
    @Test
    @DisplayName("BookDocument getters y setters")
    void bookDocument() {
        BookDocument d = new BookDocument();
        d.setId("b1");
        d.setTitle("T");
        d.setAuthor("A");
        d.setIsbn("isbn");
        d.setCategories(List.of("C1"));
        d.setPublicationType("LIBRO");
        d.setPublicationDate(LocalDate.now());
        d.setAddedToCatalogDate(LocalDate.now());
        d.setMetaData(new BookDocument.MetaData(100, "ES", "Pub"));
        d.setAvailability(new BookDocument.Availability("AVAILABLE", 5, 3, 2));

        assertEquals("b1", d.getId());
        assertEquals("isbn", d.getIsbn());
        assertEquals(1, d.getCategories().size());
        assertEquals(100, d.getMetaData().getPages());
        assertEquals("ES", d.getMetaData().getLanguage());
        assertEquals("Pub", d.getMetaData().getPublisher());
        assertEquals("AVAILABLE", d.getAvailability().getStatus());
        assertEquals(5, d.getAvailability().getTotalCopies());
        assertEquals(3, d.getAvailability().getAvailableCopies());
        assertEquals(2, d.getAvailability().getBorrowedCopies());
    }

    @Test
    @DisplayName("BookDocument MetaData constructor vacío")
    void bookDocument_metaData() {
        BookDocument.MetaData m = new BookDocument.MetaData();
        m.setPages(50);
        m.setLanguage("EN");
        m.setPublisher("P");
        assertEquals(50, m.getPages());
    }

    @Test
    @DisplayName("BookDocument Availability constructor vacío")
    void bookDocument_availability() {
        BookDocument.Availability a = new BookDocument.Availability();
        a.setStatus("UNAVAILABLE");
        a.setTotalCopies(10);
        a.setAvailableCopies(0);
        a.setBorrowedCopies(10);
        assertEquals("UNAVAILABLE", a.getStatus());
    }

    // --- UserDocument ---
    @Test
    @DisplayName("UserDocument getters y setters")
    void userDocument() {
        UserDocument d = new UserDocument();
        d.setId("u1");
        d.setName("N");
        d.setUsername("un");
        d.setPassword("p");
        d.setRole("USER");
        d.setEmail("e@m.com");
        d.setMembershipType("VIP");
        d.setRegistrationDate(LocalDate.now());
        assertEquals("u1", d.getId());
        assertEquals("USER", d.getRole());
        assertEquals("e@m.com", d.getEmail());
        assertEquals("VIP", d.getMembershipType());

        UserDocument d2 = new UserDocument("u2", "N2", "un2", "p2", "LIBRARIAN", "e2@m.com", "STANDARD", LocalDate.now());
        assertEquals("LIBRARIAN", d2.getRole());
    }

    // --- LoanDocument ---
    @Test
    @DisplayName("LoanDocument getters y setters")
    void loanDocument() {
        LoanDocument d = new LoanDocument();
        d.setId("l1");
        d.setBookId("b1");
        d.setUserId("u1");
        d.setLoanDate(LocalDate.now());
        d.setStatus("ACTIVE");
        d.setReturnDate(null);
        List<LoanDocument.LoanHistory> history = new ArrayList<>();
        history.add(new LoanDocument.LoanHistory("ACTIVE", LocalDate.now()));
        d.setHistory(history);

        assertEquals("l1", d.getId());
        assertEquals("b1", d.getBookId());
        assertEquals(1, d.getHistory().size());
        assertEquals("ACTIVE", d.getHistory().get(0).getStatus());
        assertNotNull(d.getHistory().get(0).getDate());

        LoanDocument.LoanHistory h = new LoanDocument.LoanHistory();
        h.setStatus("RETURNED");
        h.setDate(LocalDate.now());
        assertEquals("RETURNED", h.getStatus());
    }

    @Test
    @DisplayName("LoanDocument constructor completo")
    void loanDocument_allArgs() {
        List<LoanDocument.LoanHistory> history = new ArrayList<>();
        LoanDocument d = new LoanDocument("l1", "b1", "u1", LocalDate.now(), "ACTIVE", null, history);
        assertEquals("l1", d.getId());
    }

    // --- DTO tests ---
    @Test
    @DisplayName("LoginRequest getters")
    void loginRequest() {
        edu.eci.dosw.tdd.controller.dto.LoginRequest r = new edu.eci.dosw.tdd.controller.dto.LoginRequest();
        r.setUsername("u");
        r.setPassword("p");
        assertEquals("u", r.getUsername());
        assertEquals("p", r.getPassword());

        edu.eci.dosw.tdd.controller.dto.LoginRequest r2 = new edu.eci.dosw.tdd.controller.dto.LoginRequest("u2", "p2");
        assertEquals("u2", r2.getUsername());
    }

    @Test
    @DisplayName("LoginResponse getters")
    void loginResponse() {
        edu.eci.dosw.tdd.controller.dto.LoginResponse r = new edu.eci.dosw.tdd.controller.dto.LoginResponse();
        r.setToken("t");
        r.setUsername("u");
        r.setRole("R");
        assertEquals("t", r.getToken());

        edu.eci.dosw.tdd.controller.dto.LoginResponse r2 = new edu.eci.dosw.tdd.controller.dto.LoginResponse("t2", "u2", "R2");
        assertEquals("t2", r2.getToken());
    }

    @Test
    @DisplayName("RegisterRequest getters")
    void registerRequest() {
        edu.eci.dosw.tdd.controller.dto.RegisterRequest r = new edu.eci.dosw.tdd.controller.dto.RegisterRequest();
        r.setName("n");
        r.setUsername("u");
        r.setPassword("p");
        r.setEmail("e");
        r.setMembershipType("VIP");
        assertEquals("n", r.getName());
        assertEquals("e", r.getEmail());

        edu.eci.dosw.tdd.controller.dto.RegisterRequest r2 = new edu.eci.dosw.tdd.controller.dto.RegisterRequest("n2", "u2", "p2", "e2", "STANDARD");
        assertEquals("n2", r2.getName());
    }

    @Test
    @DisplayName("LoanDTO.LoanHistoryDTO")
    void loanHistoryDTO() {
        edu.eci.dosw.tdd.controller.dto.LoanDTO.LoanHistoryDTO h = new edu.eci.dosw.tdd.controller.dto.LoanDTO.LoanHistoryDTO();
        h.setStatus("ACTIVE");
        h.setDate(LocalDate.now());
        assertEquals("ACTIVE", h.getStatus());

        edu.eci.dosw.tdd.controller.dto.LoanDTO.LoanHistoryDTO h2 = new edu.eci.dosw.tdd.controller.dto.LoanDTO.LoanHistoryDTO("RETURNED", LocalDate.now());
        assertEquals("RETURNED", h2.getStatus());
    }
}
