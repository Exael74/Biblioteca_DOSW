package edu.eci.dosw.tdd.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    private String id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LoanStatus status;
    private LocalDate returnDate;

    // Campo extendido para MongoDB - historial del préstamo
    private List<LoanHistoryEntry> history = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanHistoryEntry {
        private String status;
        private LocalDate date;
    }
}