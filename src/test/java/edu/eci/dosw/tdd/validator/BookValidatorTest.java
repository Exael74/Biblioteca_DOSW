package edu.eci.dosw.tdd.validator;

import edu.eci.dosw.tdd.core.model.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BookValidatorTest {

    private final BookValidator bookValidator = new BookValidator();

    @Test
    void shouldRejectNegativeTotalCopies() {
        Book invalidBook = Book.builder()
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .totalCopies(-1)
                .availableCopies(0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> bookValidator.validateBookForCreation(invalidBook));
    }

    @Test
    void shouldRejectNegativeAvailableCopies() {
        Book invalidBook = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .totalCopies(5)
                .availableCopies(-1)
                .build();

        assertThrows(IllegalArgumentException.class, () -> bookValidator.validateBookForCreation(invalidBook));
    }

    @Test
    void shouldRejectAvailableCopiesGreaterThanTotalCopies() {
        Book invalidBook = Book.builder()
                .title("Refactoring")
                .author("Martin Fowler")
                .totalCopies(2)
                .availableCopies(3)
                .build();

        assertThrows(IllegalArgumentException.class, () -> bookValidator.validateBookForCreation(invalidBook));
    }
}