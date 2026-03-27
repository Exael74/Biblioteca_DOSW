package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {
    long countByUser_IdAndStatus(String userId, Loan.Status status);
}
