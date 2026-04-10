package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookDocumentMapper {

    public Book toDomain(BookDocument doc) {
        if (doc == null) return null;

        Book book = new Book();
        book.setId(doc.getId());
        book.setTitle(doc.getTitle());
        book.setAuthor(doc.getAuthor());
        book.setIsbn(doc.getIsbn());
        book.setCategories(doc.getCategories());
        book.setPublicationType(doc.getPublicationType());
        book.setPublicationDate(doc.getPublicationDate());
        book.setAddedToCatalogDate(doc.getAddedToCatalogDate());

        if (doc.getMetaData() != null) {
            book.setPages(doc.getMetaData().getPages());
            book.setLanguage(doc.getMetaData().getLanguage());
            book.setPublisher(doc.getMetaData().getPublisher());
        }

        if (doc.getAvailability() != null) {
            book.setTotalStock(doc.getAvailability().getTotalCopies());
            book.setAvailableCopies(doc.getAvailability().getAvailableCopies());
        }

        return book;
    }

    public BookDocument toDocument(Book book) {
        if (book == null) return null;

        BookDocument doc = new BookDocument();
        doc.setId(book.getId());
        doc.setTitle(book.getTitle());
        doc.setAuthor(book.getAuthor());
        doc.setIsbn(book.getIsbn());
        doc.setCategories(book.getCategories());
        doc.setPublicationType(book.getPublicationType());
        doc.setPublicationDate(book.getPublicationDate());
        doc.setAddedToCatalogDate(book.getAddedToCatalogDate() != null
                ? book.getAddedToCatalogDate() : LocalDate.now());

        BookDocument.MetaData metaData = new BookDocument.MetaData();
        metaData.setPages(book.getPages());
        metaData.setLanguage(book.getLanguage());
        metaData.setPublisher(book.getPublisher());
        doc.setMetaData(metaData);

        BookDocument.Availability availability = new BookDocument.Availability();
        availability.setTotalCopies(book.getTotalStock());
        availability.setAvailableCopies(book.getAvailableCopies());
        availability.setBorrowedCopies(book.getTotalStock() - book.getAvailableCopies());
        availability.setStatus(book.getAvailableCopies() > 0 ? "AVAILABLE" : "UNAVAILABLE");
        doc.setAvailability(availability);

        return doc;
    }
}
