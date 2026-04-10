package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import org.springframework.stereotype.Component;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;

@Component
public class BookMongoMapper {

    public Book toDomain(BookDocument document) {
        if (document == null) {
            return null;
        }
        return new Book(
            document.getId(),
            document.getTitle(),
            document.getAuthor(),
            document.getTotalCopies(),
            document.getAvailableCopies()
        );
    }

    public BookDocument toDocument(Book domain) {
        if (domain == null) {
            return null;
        }
        return BookDocument.builder()
            .id(domain.getId())
            .title(domain.getTitle())
            .author(domain.getAuthor())
            .totalCopies(domain.getTotalCopies())
            .availableCopies(domain.getAvailableCopies())
            .build();
    }
}
