package edu.eci.dosw.tdd.persistence.nonrelational.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class BookDocument {

    @Id
    private String id;

    private String title;

    private String author;

    private String isbn;

    private List<String> categories;

    private String publicationType; // REVISTA, EBOOK, CARTILLA, LIBRO, etc.

    private LocalDate publicationDate;

    private MetaData metaData;

    private Availability availability;

    private LocalDate addedToCatalogDate;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetaData {
        private int pages;
        private String language;
        private String publisher;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Availability {
        private String status; // AVAILABLE, UNAVAILABLE
        private int totalCopies;
        private int availableCopies;
        private int borrowedCopies;
    }
}
