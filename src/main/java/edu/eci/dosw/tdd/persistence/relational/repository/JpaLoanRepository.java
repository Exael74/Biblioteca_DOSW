package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLoanRepository extends JpaRepository<LoanEntity, String> {
}
