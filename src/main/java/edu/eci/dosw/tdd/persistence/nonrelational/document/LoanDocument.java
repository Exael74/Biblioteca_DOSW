package edu.eci.dosw.tdd.persistence.nonrelational.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loans")
public class LoanDocument {

    @Id
    private String id;

    private String bookId;

    private String userId;

    private LocalDate loanDate;

    private String status; // ACTIVE, RETURNED

    private LocalDate returnDate;

    private List<LoanHistory> history = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanHistory {
        private String status;
        private LocalDate date;
    }
}
