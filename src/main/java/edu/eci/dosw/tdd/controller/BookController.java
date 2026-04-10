package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.CreateBookRequest;
import edu.eci.dosw.tdd.controller.dto.UpdateInventoryRequest;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Secured("ROLE_LIBRARIAN")
    @PostMapping
    public Book createBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.addBook(Book.builder()
                .title(request.title())
                .author(request.author())
                .totalCopies(request.totalCopies())
                .availableCopies(request.availableCopies())
                .build());
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PatchMapping("/{id}/inventory")
    public Book updateInventory(@PathVariable String id, @Valid @RequestBody UpdateInventoryRequest request) {
        return bookService.updateInventory(id, request.totalCopies(), request.availableCopies());
    }

    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    @GetMapping("/available")
    public List<Book> getAvailableBooks() {
        return bookService.getAvailableBooks();
    }
}