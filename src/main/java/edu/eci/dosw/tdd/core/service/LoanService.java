package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.mapper.LoanPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import edu.eci.dosw.tdd.util.DateUtil;
import edu.eci.dosw.tdd.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.validator.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final BookService bookService;
    private final UserService userService;
    private final LoanValidator loanValidator;
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanPersistenceMapper loanMapper;

    @Transactional
    public Loan borrowBook(String bookId, String userId) {
        Book book = bookService.getBookById(bookId);
        User user = userService.getUserById(userId);
        long activeLoans = loanRepository.countByUser_IdAndStatus(userId, Loan.Status.ACTIVE);
        loanValidator.validateLoanCreation(user, activeLoans);
        bookService.decreaseAvailableStock(bookId);

        LocalDateTime loanDate = DateUtil.getCurrentDateTime();
        
        Loan loan = Loan.builder()
                .id(IdGeneratorUtil.generateId())
                .book(book)
                .user(user)
                .loanDate(loanDate)
                .returnDate(DateUtil.getReturnDate(loanDate, 14)) // 14 days standard loan
                .status(Loan.Status.ACTIVE)
                .build();

        LoanEntity loanEntity = LoanEntity.builder()
            .id(loan.getId())
            .book(bookRepository.findById(bookId).orElseThrow())
            .user(userRepository.findById(userId).orElseThrow())
            .loanDate(loan.getLoanDate())
            .returnDate(loan.getReturnDate())
            .status(loan.getStatus())
            .build();

        LoanEntity saved = loanRepository.save(loanEntity);
        return loanMapper.toDomain(saved);
    }

    @Transactional
    public Loan returnBook(String loanId) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + loanId + " not found."));
        if (loan.getStatus() == Loan.Status.RETURNED) {
            throw new IllegalArgumentException("Loan is already returned.");
        }

        loan.setStatus(Loan.Status.RETURNED);
        bookService.increaseAvailableStock(loan.getBook().getId());

        LoanEntity saved = loanRepository.save(loan);
        return loanMapper.toDomain(saved);
    }

    @Transactional
    public Loan returnBookForUser(String loanId, String userId) {
        LoanEntity loan = loanRepository.findByIdAndUser_Id(loanId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + loanId + " not found for user."));
        if (loan.getStatus() == Loan.Status.RETURNED) {
            throw new IllegalArgumentException("Loan is already returned.");
        }
        loan.setStatus(Loan.Status.RETURNED);
        bookService.increaseAvailableStock(loan.getBook().getId());
        LoanEntity saved = loanRepository.save(loan);
        return loanMapper.toDomain(saved);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll().stream().map(loanMapper::toDomain).toList();
    }

    public List<Loan> getLoansByUser(String userId) {
        return loanRepository.findByUser_Id(userId).stream().map(loanMapper::toDomain).toList();
    }
}
