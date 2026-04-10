package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MongoBookRepository extends MongoRepository<BookDocument, String> {
    List<BookDocument> findByCategories(String category);
    List<BookDocument> findByPublicationType(String publicationType);
}
