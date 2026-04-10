package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.ApiMessages;
import edu.eci.dosw.tdd.persistence.BookRepository;
import edu.eci.dosw.tdd.persistence.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public Book addBook(Book book) {
        if (bookRepository.existsById(book.getId())) {
            throw new IllegalArgumentException(ApiMessages.BOOK_ALREADY_EXISTS);
        }
        if (book.getTotalStock() <= 0) {
            throw new IllegalArgumentException(ApiMessages.BOOK_INVALID_STOCK);
        }
        if (book.getAvailableCopies() < 0 || book.getAvailableCopies() > book.getTotalStock()) {
            throw new IllegalArgumentException(ApiMessages.BOOK_INVALID_COPIES);
        }
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotAvailableException(ApiMessages.BOOK_NOT_FOUND));
    }

    @Transactional
    public Book updateStock(String id, int totalStock, int availableCopies) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotAvailableException(ApiMessages.BOOK_NOT_FOUND));
        if (totalStock <= 0) {
            throw new IllegalArgumentException(ApiMessages.BOOK_INVALID_STOCK);
        }
        if (availableCopies < 0 || availableCopies > totalStock) {
            throw new IllegalArgumentException(ApiMessages.BOOK_INVALID_COPIES);
        }
        book.setTotalStock(totalStock);
        book.setAvailableCopies(availableCopies);
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(String id) {
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotAvailableException(ApiMessages.BOOK_NOT_FOUND));
        if (loanRepository.existsByBookIdAndStatus(id, "ACTIVE")) {
            throw new IllegalStateException(ApiMessages.BOOK_HAS_ACTIVE_LOANS);
        }
        loanRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }
}