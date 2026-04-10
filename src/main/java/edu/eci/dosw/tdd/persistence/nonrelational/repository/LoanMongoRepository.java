package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;

@Repository
public interface LoanMongoRepository extends MongoRepository<LoanDocument, String> {
    long countByUserIdAndStatus(String userId, String status);
    List<LoanDocument> findByUserId(String userId);
    Optional<LoanDocument> findByIdAndUserId(String id, String userId);
}
