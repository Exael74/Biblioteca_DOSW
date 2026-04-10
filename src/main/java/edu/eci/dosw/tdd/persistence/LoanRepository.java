package edu.eci.dosw.tdd.persistence;

import edu.eci.dosw.tdd.core.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(String id);

    List<Loan> findAll();

    List<Loan> findByUserId(String userId);

    boolean existsByBookIdAndStatus(String bookId, String status);

    boolean existsByUserIdAndStatus(String userId, String status);

    void deleteById(String id);

    void deleteByBookId(String bookId);

    void deleteByUserId(String userId);
}
