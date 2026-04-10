package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.ApiMessages;
import edu.eci.dosw.tdd.persistence.BookRepository;
import edu.eci.dosw.tdd.persistence.LoanRepository;
import edu.eci.dosw.tdd.persistence.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Loan createLoan(String bookId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ApiMessages.USER_NOT_FOUND));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotAvailableException(ApiMessages.BOOK_NOT_FOUND));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(ApiMessages.BOOK_NOT_AVAILABLE);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setId(UUID.randomUUID().toString());
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);

        // Agregar entrada al historial
        List<Loan.LoanHistoryEntry> history = new ArrayList<>();
        history.add(new Loan.LoanHistoryEntry("ACTIVE", LocalDate.now()));
        loan.setHistory(history);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnLoan(String loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException(ApiMessages.LOAN_NOT_FOUND));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException(ApiMessages.LOAN_ALREADY_RETURNED);
        }

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        // Agregar entrada al historial
        if (loan.getHistory() == null) {
            loan.setHistory(new ArrayList<>());
        }
        loan.getHistory().add(new Loan.LoanHistoryEntry("RETURNED", LocalDate.now()));

        Book book = bookRepository.findById(loan.getBook().getId())
                .orElseThrow(() -> new BookNotAvailableException(ApiMessages.BOOK_NOT_FOUND));

        if (book.getAvailableCopies() < book.getTotalStock()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }

        return loanRepository.save(loan);
    }

    @Transactional
    public void deleteLoan(String loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException(ApiMessages.LOAN_NOT_FOUND));
        if (loan.getStatus() == LoanStatus.ACTIVE) {
            throw new IllegalStateException(ApiMessages.LOAN_CANNOT_DELETE_ACTIVE);
        }
        loanRepository.deleteById(loanId);
    }

    public void validateLoanOwnership(String loanId, String currentUserId, boolean isLibrarian) {
        if (isLibrarian) return;
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException(ApiMessages.LOAN_NOT_FOUND));
        if (!loan.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("No puedes operar sobre préstamos de otros usuarios");
        }
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getLoansByUserId(String userId) {
        return loanRepository.findByUserId(userId);
    }
}