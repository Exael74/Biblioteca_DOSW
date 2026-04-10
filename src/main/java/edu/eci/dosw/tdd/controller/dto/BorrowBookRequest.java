package edu.eci.dosw.tdd.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record BorrowBookRequest(
        @NotBlank(message = "Book id is required") String bookId
) {
}