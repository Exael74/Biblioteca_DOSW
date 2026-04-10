package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.BookPersistenceMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.validator.BookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookValidator bookValidator;
    private final BookRepository bookRepository;
    private final BookPersistenceMapper bookMapper;

    @Transactional
    public Book addBook(Book book) {
        bookValidator.validateBookForCreation(book);
        if (book.getId() == null) {
            book.setId(IdGeneratorUtil.generateId());
        }
        BookEntity saved = bookRepository.save(bookMapper.toEntity(book));
        return bookMapper.toDomain(saved);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll().stream().map(bookMapper::toDomain).toList();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0).stream().map(bookMapper::toDomain).toList();
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDomain)
                .orElseThrow(() -> new BookNotAvailableException("Book with ID " + id + " not found."));
    }

    @Transactional
    public Book decreaseAvailableStock(String id) {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotAvailableException("Book with ID " + id + " not found."));
        Book book = bookMapper.toDomain(entity);
        bookValidator.validateBookForLoan(book);
        entity.setAvailableCopies(entity.getAvailableCopies() - 1);
        return bookMapper.toDomain(bookRepository.save(entity));
    }

    @Transactional
    public Book increaseAvailableStock(String id) {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotAvailableException("Book with ID " + id + " not found."));
        if (entity.getAvailableCopies() >= entity.getTotalCopies()) {
            throw new IllegalStateException("Cannot increase available copies beyond total copies");
        }
        entity.setAvailableCopies(entity.getAvailableCopies() + 1);
        return bookMapper.toDomain(bookRepository.save(entity));
    }

    @Transactional
    public Book updateInventory(String id, int totalCopies, int availableCopies) {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotAvailableException("Book with ID " + id + " not found."));
        Book candidate = Book.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
        bookValidator.validateBookForCreation(candidate);
        entity.setTotalCopies(totalCopies);
        entity.setAvailableCopies(availableCopies);
        return bookMapper.toDomain(bookRepository.save(entity));
    }
}