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
public class LoanDTO {

    private String id;
    private String bookId;
    private String bookTitle;
    private String userId;
    private String userName;
    private LocalDate loanDate;
    private String status;
    private LocalDate returnDate;
    private List<LoanHistoryDTO> history;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanHistoryDTO {
        private String status;
        private LocalDate date;
    }
}
