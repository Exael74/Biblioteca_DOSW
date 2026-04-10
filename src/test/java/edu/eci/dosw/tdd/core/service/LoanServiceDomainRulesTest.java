package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class LoanServiceDomainRulesTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private String userId;
    private String bookId;

    @BeforeEach
    void setup() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .name("Ana")
                .username("ana")
                .password("secret")
                .role(User.Role.USER)
                .build();
        userId = userService.addUser(user).getId();

        Book book = Book.builder()
                .title("Microservices Patterns")
                .author("Chris Richardson")
                .totalCopies(2)
                .availableCopies(2)
                .build();
        bookId = bookService.addBook(book).getId();
    }

    @Test
    void shouldDecreaseStockWhenBookIsBorrowed() {
        loanService.borrowBook(bookId, userId);

        assertEquals(1, bookService.getBookById(bookId).getAvailableCopies());
        assertEquals(1, loanRepository.count());
    }

    @Test
    void shouldNotBorrowBookWithoutAvailability() {
        loanService.borrowBook(bookId, userId);
        loanService.borrowBook(bookId, userId);

        assertThrows(BookNotAvailableException.class, () -> loanService.borrowBook(bookId, userId));
    }

    @Test
    void shouldNotBorrowNonExistentBook() {
        assertThrows(BookNotAvailableException.class, () -> loanService.borrowBook("missing-book", userId));
    }

    @Test
    void shouldIncreaseStockOnReturnAndNotExceedOriginalStock() {
        Loan loan = loanService.borrowBook(bookId, userId);
        loanService.returnBook(loan.getId());

        assertEquals(2, bookService.getBookById(bookId).getAvailableCopies());
        assertEquals(Loan.Status.RETURNED, loanRepository.findById(loan.getId()).orElseThrow().getStatus());
        assertThrows(IllegalStateException.class, () -> bookService.increaseAvailableStock(bookId));
    }

    @Test
    void shouldNotReturnAlreadyReturnedLoan() {
        Loan loan = loanService.borrowBook(bookId, userId);
        loanService.returnBook(loan.getId());

        assertThrows(IllegalArgumentException.class, () -> loanService.returnBook(loan.getId()));
    }
}