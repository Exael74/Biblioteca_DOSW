package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO) {
        Book book = bookMapper.toDomain(bookDTO);
        Book savedBook = bookService.addBook(book);
        return new ResponseEntity<>(bookMapper.toDTO(savedBook), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(bookMapper.toDTO(book));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PutMapping("/{id}/stock")
    public ResponseEntity<BookDTO> updateStock(@PathVariable String id,
                                               @RequestParam int totalStock,
                                               @RequestParam int availableCopies) {
        Book updated = bookService.updateStock(id, totalStock, availableCopies);
        return ResponseEntity.ok(bookMapper.toDTO(updated));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}