package edu.eci.dosw.tdd.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String id;
    private String title;
    private String author;

    @Builder.Default
    private int totalCopies = 1;

    @Builder.Default
    private int availableCopies = 1;
}
