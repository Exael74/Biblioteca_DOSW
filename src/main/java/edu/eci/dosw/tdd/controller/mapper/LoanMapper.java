package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.model.Loan;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class LoanMapper {

    public LoanDTO toDTO(Loan loan) {
        if (loan == null) return null;
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setBookId(loan.getBook() != null ? loan.getBook().getId() : null);
        dto.setBookTitle(loan.getBook() != null ? loan.getBook().getTitle() : null);
        dto.setUserId(loan.getUser() != null ? loan.getUser().getId() : null);
        dto.setUserName(loan.getUser() != null ? loan.getUser().getName() : null);
        dto.setLoanDate(loan.getLoanDate());
        dto.setStatus(loan.getStatus() != null ? loan.getStatus().name() : null);
        dto.setReturnDate(loan.getReturnDate());

        if (loan.getHistory() != null && !loan.getHistory().isEmpty()) {
            dto.setHistory(loan.getHistory().stream()
                    .map(h -> new LoanDTO.LoanHistoryDTO(h.getStatus(), h.getDate()))
                    .collect(Collectors.toList()));
        } else {
            dto.setHistory(Collections.emptyList());
        }

        return dto;
    }
}
