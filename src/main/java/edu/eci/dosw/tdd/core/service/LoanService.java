package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.util.DateUtil;
import edu.eci.dosw.tdd.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.validator.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final BookService bookService;
    private final UserService userService;
    private final LoanValidator loanValidator;
    
    // In-memory persistence
    private final Map<String, Loan> loanMap = new ConcurrentHashMap<>();

    public Loan borrowBook(String bookId, String userId) {
        Book book = bookService.getBookById(bookId);
        bookService.updateAvailability(bookId, false); // Book validation handles availability check
        
        User user = userService.getUserById(userId);
        loanValidator.validateLoanCreation(user);

        LocalDateTime loanDate = DateUtil.getCurrentDateTime();
        
        Loan loan = Loan.builder()
                .id(IdGeneratorUtil.generateId())
                .book(book)
                .user(user)
                .loanDate(loanDate)
                .returnDate(DateUtil.getReturnDate(loanDate, 14)) // 14 days standard loan
                .status(Loan.Status.ACTIVE)
                .build();
                
        user.getLoans().add(loan);
        loanMap.put(loan.getId(), loan);

        return loan;
    }

    public Loan returnBook(String loanId) {
        Loan loan = loanMap.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan with ID " + loanId + " not found.");
        }
        if (loan.getStatus() == Loan.Status.RETURNED) {
            throw new IllegalArgumentException("Loan is already returned.");
        }

        loan.setStatus(Loan.Status.RETURNED);
        bookService.updateAvailability(loan.getBook().getId(), true);
        
        return loan;
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loanMap.values());
    }
}
