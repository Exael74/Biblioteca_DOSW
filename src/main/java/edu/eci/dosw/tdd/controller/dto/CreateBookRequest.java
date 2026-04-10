package edu.eci.dosw.tdd.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateBookRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Author is required") String author,
        @Min(value = 0, message = "Total copies cannot be negative") int totalCopies,
        @Min(value = 0, message = "Available copies cannot be negative") int availableCopies
) {
}