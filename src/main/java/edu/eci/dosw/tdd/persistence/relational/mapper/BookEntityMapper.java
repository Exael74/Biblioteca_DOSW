package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookEntityMapper {

    public Book toDomain(BookEntity entity) {
        if (entity == null) return null;
        Book book = new Book();
        book.setId(entity.getId());
        book.setTitle(entity.getTitle());
        book.setAuthor(entity.getAuthor());
        book.setTotalStock(entity.getTotalStock());
        book.setAvailableCopies(entity.getAvailableCopies());
        return book;
    }

    public BookEntity toEntity(Book domain) {
        if (domain == null) return null;
        return new BookEntity(
                domain.getId(),
                domain.getTitle(),
                domain.getAuthor(),
                domain.getTotalStock(),
                domain.getAvailableCopies()
        );
    }
}
