package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.validator.BookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookValidator bookValidator;
    
    // In-Memory database
    private final Map<String, Book> bookMap = new ConcurrentHashMap<>();

    public Book addBook(Book book) {
        bookValidator.validateBookForCreation(book);
        if (book.getId() == null) {
            book.setId(IdGeneratorUtil.generateId());
        }
        bookMap.put(book.getId(), book);
        return book;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(bookMap.values());
    }

    public Book getBookById(String id) {
        Book book = bookMap.get(id);
        if (book == null) {
            throw new BookNotAvailableException("Book with ID " + id + " not found.");
        }
        return book;
    }

    public Book updateAvailability(String id, boolean available) {
        Book book = getBookById(id);
        book.setAvailable(available);
        return book;
    }
}
