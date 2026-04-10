package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanEntityMapper {

    private final BookEntityMapper bookEntityMapper;
    private final UserEntityMapper userEntityMapper;

    public LoanEntityMapper(BookEntityMapper bookEntityMapper, UserEntityMapper userEntityMapper) {
        this.bookEntityMapper = bookEntityMapper;
        this.userEntityMapper = userEntityMapper;
    }

    public Loan toDomain(LoanEntity entity) {
        if (entity == null) return null;
        Loan loan = new Loan();
        loan.setId(entity.getId());
        loan.setBook(bookEntityMapper.toDomain(entity.getBook()));
        loan.setUser(userEntityMapper.toDomain(entity.getUser()));
        loan.setLoanDate(entity.getLoanDate());
        loan.setStatus(entity.getStatus());
        loan.setReturnDate(entity.getReturnDate());
        return loan;
    }

    public LoanEntity toEntity(Loan domain) {
        if (domain == null) return null;
        return new LoanEntity(
                domain.getId(),
                bookEntityMapper.toEntity(domain.getBook()),
                userEntityMapper.toEntity(domain.getUser()),
                domain.getLoanDate(),
                domain.getStatus(),
                domain.getReturnDate()
        );
    }
}
