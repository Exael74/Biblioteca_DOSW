package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.BookRepository;
import edu.eci.dosw.tdd.persistence.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId("book-001");
        testBook.setTitle("Clean Code");
        testBook.setAuthor("Robert C. Martin");
        testBook.setTotalStock(5);
        testBook.setAvailableCopies(5);
    }

    // --- addBook ---

    @Test
    @DisplayName("Agregar libro exitosamente")
    void addBook_success() {
        when(bookRepository.existsById("book-001")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addBook(testBook);

        assertNotNull(result);
        assertEquals("book-001", result.getId());
        assertEquals("Clean Code", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Agregar libro con ID duplicado lanza excepción")
    void addBook_duplicateId_throwsException() {
        when(bookRepository.existsById("book-001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(testBook));
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Agregar libro con stock 0 lanza excepción")
    void addBook_zeroStock_throwsException() {
        testBook.setTotalStock(0);
        when(bookRepository.existsById("book-001")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(testBook));
    }

    @Test
    @DisplayName("Agregar libro con stock negativo lanza excepción")
    void addBook_negativeStock_throwsException() {
        testBook.setTotalStock(-1);
        when(bookRepository.existsById("book-001")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(testBook));
    }

    @Test
    @DisplayName("Agregar libro con copias negativas lanza excepción")
    void addBook_negativeCopies_throwsException() {
        testBook.setAvailableCopies(-1);
        when(bookRepository.existsById("book-001")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(testBook));
    }

    @Test
    @DisplayName("Agregar libro con copias mayores que stock lanza excepción")
    void addBook_copiesExceedStock_throwsException() {
        testBook.setAvailableCopies(10);
        testBook.setTotalStock(5);
        when(bookRepository.existsById("book-001")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(testBook));
    }

    // --- getAllBooks ---

    @Test
    @DisplayName("Obtener todos los libros retorna lista")
    void getAllBooks_returnsList() {
        when(bookRepository.findAll()).thenReturn(List.of(testBook));

        List<Book> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("book-001", result.get(0).getId());
    }

    @Test
    @DisplayName("Obtener todos los libros retorna lista vacía")
    void getAllBooks_returnsEmptyList() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<Book> result = bookService.getAllBooks();

        assertTrue(result.isEmpty());
    }

    // --- getBookById ---

    @Test
    @DisplayName("Obtener libro por ID exitosamente")
    void getBookById_success() {
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));

        Book result = bookService.getBookById("book-001");

        assertEquals("book-001", result.getId());
    }

    @Test
    @DisplayName("Obtener libro por ID inexistente lanza excepción")
    void getBookById_notFound_throwsException() {
        when(bookRepository.findById("book-999")).thenReturn(Optional.empty());

        assertThrows(BookNotAvailableException.class, () -> bookService.getBookById("book-999"));
    }

    // --- updateStock ---

    @Test
    @DisplayName("Actualizar stock exitosamente")
    void updateStock_success() {
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book result = bookService.updateStock("book-001", 10, 8);

        assertEquals(10, result.getTotalStock());
        assertEquals(8, result.getAvailableCopies());
    }

    @Test
    @DisplayName("Actualizar stock con libro inexistente lanza excepción")
    void updateStock_notFound_throwsException() {
        when(bookRepository.findById("book-999")).thenReturn(Optional.empty());

        assertThrows(BookNotAvailableException.class, () -> bookService.updateStock("book-999", 10, 8));
    }

    @Test
    @DisplayName("Actualizar stock con stock inválido lanza excepción")
    void updateStock_invalidStock_throwsException() {
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));

        assertThrows(IllegalArgumentException.class, () -> bookService.updateStock("book-001", 0, 0));
    }

    @Test
    @DisplayName("Actualizar stock con copias inválidas lanza excepción")
    void updateStock_invalidCopies_throwsException() {
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));

        assertThrows(IllegalArgumentException.class, () -> bookService.updateStock("book-001", 5, 10));
    }

    @Test
    @DisplayName("Actualizar stock con copias negativas lanza excepción")
    void updateStock_negativeCopies_throwsException() {
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));

        assertThrows(IllegalArgumentException.class, () -> bookService.updateStock("book-001", 5, -1));
    }

    // --- deleteBook ---

    @Test
    @DisplayName("Eliminar libro exitosamente")
    void deleteBook_success() {
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));
        when(loanRepository.existsByBookIdAndStatus("book-001", "ACTIVE")).thenReturn(false);

        assertDoesNotThrow(() -> bookService.deleteBook("book-001"));
        verify(bookRepository).deleteById("book-001");
    }

    @Test
    @DisplayName("Eliminar libro con préstamos activos lanza excepción")
    void deleteBook_activeLoans_throwsException() {
        when(bookRepository.findById("book-001")).thenReturn(Optional.of(testBook));
        when(loanRepository.existsByBookIdAndStatus("book-001", "ACTIVE")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> bookService.deleteBook("book-001"));
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar libro inexistente lanza excepción")
    void deleteBook_notFound_throwsException() {
        when(bookRepository.findById("book-999")).thenReturn(Optional.empty());

        assertThrows(BookNotAvailableException.class, () -> bookService.deleteBook("book-999"));
    }
}
