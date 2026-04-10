package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.*;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.core.service.*;
import edu.eci.dosw.tdd.core.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock private BookService bookService;
    @Mock private UserService userService;
    @Mock private LoanService loanService;
    @Mock private AuthService authService;
    @Mock private BookMapper bookMapper;
    @Mock private UserMapper userMapper;
    @Mock private LoanMapper loanMapper;
    @Mock private SecurityUtils securityUtils;

    private BookController bookController;
    private UserController userController;
    private LoanController loanController;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        bookController = new BookController(bookService, bookMapper);
        userController = new UserController(userService, userMapper);
        loanController = new LoanController(loanService, loanMapper, securityUtils);
        authController = new AuthController(authService, userMapper);
    }

    // --- BookController ---

    @Test
    @DisplayName("BookController addBook retorna 201")
    void bookController_addBook() {
        BookDTO dto = new BookDTO();
        dto.setId("b1");
        Book book = new Book();
        book.setId("b1");

        when(bookMapper.toDomain(any(BookDTO.class))).thenReturn(book);
        when(bookService.addBook(any(Book.class))).thenReturn(book);
        when(bookMapper.toDTO(any(Book.class))).thenReturn(dto);

        ResponseEntity<BookDTO> response = bookController.addBook(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("b1", response.getBody().getId());
    }

    @Test
    @DisplayName("BookController getAllBooks retorna 200")
    void bookController_getAllBooks() {
        Book book = new Book();
        BookDTO dto = new BookDTO();
        when(bookService.getAllBooks()).thenReturn(List.of(book));
        when(bookMapper.toDTO(any(Book.class))).thenReturn(dto);

        ResponseEntity<List<BookDTO>> response = bookController.getAllBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("BookController getBookById retorna 200")
    void bookController_getBookById() {
        Book book = new Book();
        BookDTO dto = new BookDTO();
        dto.setId("b1");
        when(bookService.getBookById("b1")).thenReturn(book);
        when(bookMapper.toDTO(any(Book.class))).thenReturn(dto);

        ResponseEntity<BookDTO> response = bookController.getBookById("b1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("BookController updateStock retorna 200")
    void bookController_updateStock() {
        Book book = new Book();
        BookDTO dto = new BookDTO();
        when(bookService.updateStock("b1", 10, 8)).thenReturn(book);
        when(bookMapper.toDTO(any(Book.class))).thenReturn(dto);

        ResponseEntity<BookDTO> response = bookController.updateStock("b1", 10, 8);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("BookController deleteBook retorna 204")
    void bookController_deleteBook() {
        doNothing().when(bookService).deleteBook("b1");

        ResponseEntity<Void> response = bookController.deleteBook("b1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // --- UserController ---

    @Test
    @DisplayName("UserController getAllUsers retorna 200")
    void userController_getAllUsers() {
        User user = new User();
        UserDTO dto = new UserDTO();
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userMapper.toDTO(any(User.class))).thenReturn(dto);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("UserController getUserById retorna 200")
    void userController_getUserById() {
        User user = new User();
        UserDTO dto = new UserDTO();
        when(userService.getUserById("u1")).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(dto);

        ResponseEntity<UserDTO> response = userController.getUserById("u1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("UserController deleteUser retorna 204")
    void userController_deleteUser() {
        doNothing().when(userService).deleteUser("u1");

        ResponseEntity<Void> response = userController.deleteUser("u1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // --- LoanController ---

    @Test
    @DisplayName("LoanController createLoan retorna 201")
    void loanController_createLoan() {
        Loan loan = new Loan();
        LoanDTO dto = new LoanDTO();
        when(loanService.createLoan("b1", "u1")).thenReturn(loan);
        when(loanMapper.toDTO(any(Loan.class))).thenReturn(dto);

        ResponseEntity<LoanDTO> response = loanController.createLoan("b1", "u1");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("LoanController returnLoan retorna 200")
    void loanController_returnLoan() {
        Loan loan = new Loan();
        LoanDTO dto = new LoanDTO();
        doNothing().when(loanService).validateLoanOwnership(anyString(), any(), anyBoolean());
        when(securityUtils.getCurrentUserId()).thenReturn("u1");
        when(securityUtils.isLibrarian()).thenReturn(false);
        when(loanService.returnLoan("l1")).thenReturn(loan);
        when(loanMapper.toDTO(any(Loan.class))).thenReturn(dto);

        ResponseEntity<LoanDTO> response = loanController.returnLoan("l1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("LoanController getAllLoans retorna 200")
    void loanController_getAllLoans() {
        Loan loan = new Loan();
        LoanDTO dto = new LoanDTO();
        when(loanService.getAllLoans()).thenReturn(List.of(loan));
        when(loanMapper.toDTO(any(Loan.class))).thenReturn(dto);

        ResponseEntity<List<LoanDTO>> response = loanController.getAllLoans();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("LoanController getLoansByUser retorna 200")
    void loanController_getLoansByUser() {
        when(loanService.getLoansByUserId("u1")).thenReturn(Collections.emptyList());

        ResponseEntity<List<LoanDTO>> response = loanController.getLoansByUser("u1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("LoanController getMyLoans retorna 200")
    void loanController_getMyLoans() {
        when(securityUtils.getCurrentUserId()).thenReturn("u1");
        when(loanService.getLoansByUserId("u1")).thenReturn(Collections.emptyList());

        ResponseEntity<List<LoanDTO>> response = loanController.getMyLoans();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("LoanController deleteLoan retorna 204")
    void loanController_deleteLoan() {
        doNothing().when(loanService).deleteLoan("l1");

        ResponseEntity<Void> response = loanController.deleteLoan("l1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // --- AuthController ---

    @Test
    @DisplayName("AuthController login retorna 200")
    void authController_login() {
        LoginRequest req = new LoginRequest("juan", "pass");
        LoginResponse res = new LoginResponse("token", "juan", "USER");
        when(authService.login("juan", "pass")).thenReturn(res);

        ResponseEntity<LoginResponse> response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody().getToken());
    }

    @Test
    @DisplayName("AuthController register retorna 201")
    void authController_register() {
        RegisterRequest req = new RegisterRequest("Name", "user", "pass", "e@m.com", "STANDARD");
        User user = new User();
        UserDTO dto = new UserDTO();
        when(authService.register("Name", "user", "pass", "e@m.com", "STANDARD")).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(dto);

        ResponseEntity<UserDTO> response = authController.register(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("AuthController registerLibrarian retorna 201")
    void authController_registerLibrarian() {
        RegisterRequest req = new RegisterRequest("Admin", "admin", "pass", "a@m.com", "VIP");
        User user = new User();
        UserDTO dto = new UserDTO();
        when(authService.registerWithRole("Admin", "admin", "pass", "a@m.com", "VIP", Role.LIBRARIAN)).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(dto);

        ResponseEntity<UserDTO> response = authController.registerLibrarian(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
