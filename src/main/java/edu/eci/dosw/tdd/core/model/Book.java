package edu.eci.dosw.tdd.core.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @NotBlank(message = "El ID del libro es obligatorio")
    private String id;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "El autor es obligatorio")
    private String author;

    @Min(value = 1, message = "El stock total debe ser mayor a 0")
    private int totalStock = 1;

    @Min(value = 0, message = "Las copias disponibles no pueden ser negativas")
    private int availableCopies = 1;

    // Campos extendidos para MongoDB
    private String isbn;
    private List<String> categories;
    private String publicationType;
    private LocalDate publicationDate;
    private int pages;
    private String language;
    private String publisher;
    private LocalDate addedToCatalogDate;

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public int getBorrowedCopies() {
        return totalStock - availableCopies;
    }
}