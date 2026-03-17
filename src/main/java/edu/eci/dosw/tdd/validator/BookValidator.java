package edu.eci.dosw.tdd.validator;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class BookValidator {

    public void validateBookForCreation(Book book) {
        ValidationUtil.requireNotNull(book, "Book cannot be null");
        ValidationUtil.requireNonEmpty(book.getTitle(), "Book title cannot be empty");
        ValidationUtil.requireNonEmpty(book.getAuthor(), "Book author cannot be empty");
    }

    public void validateBookForLoan(Book book) {
        ValidationUtil.requireNotNull(book, "Book cannot be null");
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("Book is already loaned out");
        }
    }
}
