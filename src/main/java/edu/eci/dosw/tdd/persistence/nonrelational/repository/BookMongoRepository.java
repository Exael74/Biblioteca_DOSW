package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;

@Repository
public interface BookMongoRepository extends MongoRepository<BookDocument, String> {
    List<BookDocument> findByAvailableCopiesGreaterThan(int copies);
}
