package edu.eci.dosw.tdd.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    @DisplayName("BookNotAvailableException contiene mensaje")
    void bookNotAvailableException() {
        BookNotAvailableException ex = new BookNotAvailableException("test message");
        assertEquals("test message", ex.getMessage());
    }

    @Test
    @DisplayName("UserNotFoundException contiene mensaje")
    void userNotFoundException() {
        UserNotFoundException ex = new UserNotFoundException("user not found");
        assertEquals("user not found", ex.getMessage());
    }

    @Test
    @DisplayName("LoanLimitExceededException contiene mensaje")
    void loanLimitExceededException() {
        LoanLimitExceededException ex = new LoanLimitExceededException("limit exceeded");
        assertEquals("limit exceeded", ex.getMessage());
    }
}
