package edu.eci.dosw.tdd.controller.dto;

import jakarta.validation.constraints.Min;

public record UpdateInventoryRequest(
        @Min(value = 0, message = "Total copies cannot be negative") int totalCopies,
        @Min(value = 0, message = "Available copies cannot be negative") int availableCopies
) {
}
