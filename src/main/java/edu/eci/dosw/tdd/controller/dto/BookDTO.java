package edu.eci.dosw.tdd.controller.dto;

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
public class BookDTO {

    private String id;
    private String title;
    private String author;
    private int totalStock;
    private int availableCopies;
    private int borrowedCopies;
    private boolean available;
    private String isbn;
    private List<String> categories;
    private String publicationType;
    private LocalDate publicationDate;
    private int pages;
    private String language;
    private String publisher;
    private LocalDate addedToCatalogDate;
}
