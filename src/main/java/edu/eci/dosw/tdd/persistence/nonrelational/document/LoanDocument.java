package edu.eci.dosw.tdd.persistence.nonrelational.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loans")
public class LoanDocument {
    @Id
    private String id;
    private String bookId;
    private String userId;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    private String status;
}
