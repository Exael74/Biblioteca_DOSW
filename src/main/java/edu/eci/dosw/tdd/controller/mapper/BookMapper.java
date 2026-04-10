package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDTO toDTO(Book book) {
        if (book == null) return null;
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setTotalStock(book.getTotalStock());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setBorrowedCopies(book.getBorrowedCopies());
        dto.setAvailable(book.isAvailable());
        dto.setIsbn(book.getIsbn());
        dto.setCategories(book.getCategories());
        dto.setPublicationType(book.getPublicationType());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setPages(book.getPages());
        dto.setLanguage(book.getLanguage());
        dto.setPublisher(book.getPublisher());
        dto.setAddedToCatalogDate(book.getAddedToCatalogDate());
        return dto;
    }

    public Book toDomain(BookDTO dto) {
        if (dto == null) return null;
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setTotalStock(dto.getTotalStock());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setIsbn(dto.getIsbn());
        book.setCategories(dto.getCategories());
        book.setPublicationType(dto.getPublicationType());
        book.setPublicationDate(dto.getPublicationDate());
        book.setPages(dto.getPages());
        book.setLanguage(dto.getLanguage());
        book.setPublisher(dto.getPublisher());
        book.setAddedToCatalogDate(dto.getAddedToCatalogDate());
        return book;
    }
}
