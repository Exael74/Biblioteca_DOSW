package edu.eci.dosw.tdd.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private Book book;
    private User user;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    
    @Builder.Default
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE,
        RETURNED
    }
}
