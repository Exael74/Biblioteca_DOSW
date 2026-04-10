package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {
    long countByUser_IdAndStatus(String userId, Loan.Status status);
    List<LoanEntity> findByUser_Id(String userId);
    Optional<LoanEntity> findByIdAndUser_Id(String id, String userId);
}