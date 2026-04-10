package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import org.springframework.stereotype.Component;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;

@Component
public class LoanMongoMapper {

    // Nota: Como LoanDocument guarda IDs (referencias manuales),
    // el mapeo a Dominio requiere que le pasemos los objetos Book y User ya buscados.
    public Loan toDomain(LoanDocument document, Book book, User user) {
        if (document == null) {
            return null;
        }
        return new Loan(
            document.getId(),
            book,
            user,
            document.getLoanDate(),
            document.getReturnDate(),
            Loan.Status.valueOf(document.getStatus())
        );
    }

    public LoanDocument toDocument(Loan domain) {
        if (domain == null) {
            return null;
        }
        return LoanDocument.builder()
            .id(domain.getId())
            .bookId(domain.getBook() != null ? domain.getBook().getId() : null)
            .userId(domain.getUser() != null ? domain.getUser().getId() : null)
            .loanDate(domain.getLoanDate())
            .returnDate(domain.getReturnDate())
            .status(domain.getStatus().name())
            .build();
    }
}
