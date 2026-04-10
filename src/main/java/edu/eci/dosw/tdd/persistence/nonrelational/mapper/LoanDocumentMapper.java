package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class LoanDocumentMapper {

    public Loan toDomain(LoanDocument doc) {
        if (doc == null) return null;

        Loan loan = new Loan();
        loan.setId(doc.getId());
        loan.setLoanDate(doc.getLoanDate());
        loan.setReturnDate(doc.getReturnDate());

        if (doc.getStatus() != null) {
            loan.setStatus(LoanStatus.valueOf(doc.getStatus()));
        }

        if (doc.getBookId() != null) {
            Book book = new Book();
            book.setId(doc.getBookId());
            loan.setBook(book);
        }

        if (doc.getUserId() != null) {
            User user = new User();
            user.setId(doc.getUserId());
            loan.setUser(user);
        }

        if (doc.getHistory() != null) {
            loan.setHistory(doc.getHistory().stream()
                    .map(h -> new Loan.LoanHistoryEntry(h.getStatus(), h.getDate()))
                    .collect(Collectors.toList()));
        }

        return loan;
    }

    public LoanDocument toDocument(Loan loan) {
        if (loan == null) return null;

        LoanDocument doc = new LoanDocument();
        doc.setId(loan.getId());
        doc.setLoanDate(loan.getLoanDate());
        doc.setReturnDate(loan.getReturnDate());

        if (loan.getStatus() != null) {
            doc.setStatus(loan.getStatus().name());
        }

        if (loan.getBook() != null) {
            doc.setBookId(loan.getBook().getId());
        }

        if (loan.getUser() != null) {
            doc.setUserId(loan.getUser().getId());
        }

        if (loan.getHistory() != null) {
            doc.setHistory(loan.getHistory().stream()
                    .map(h -> new LoanDocument.LoanHistory(h.getStatus(), h.getDate()))
                    .collect(Collectors.toList()));
        } else {
            doc.setHistory(new ArrayList<>());
        }

        return doc;
    }
}
