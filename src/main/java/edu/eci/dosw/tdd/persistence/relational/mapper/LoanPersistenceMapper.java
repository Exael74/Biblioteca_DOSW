package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanPersistenceMapper {

    private final BookPersistenceMapper bookMapper;
    private final UserPersistenceMapper userMapper;

    public LoanPersistenceMapper(BookPersistenceMapper bookMapper, UserPersistenceMapper userMapper) {
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
    }

    public LoanEntity toEntity(Loan loan) {
        if (loan == null) {
            return null;
        }
        return LoanEntity.builder()
                .id(loan.getId())
                .book(bookMapper.toEntity(loan.getBook()))
                .user(userMapper.toEntity(loan.getUser()))
                .loanDate(loan.getLoanDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .build();
    }

    public Loan toDomain(LoanEntity entity) {
        if (entity == null) {
            return null;
        }
        return Loan.builder()
                .id(entity.getId())
                .book(bookMapper.toDomain(entity.getBook()))
                .user(userMapper.toDomain(entity.getUser()))
                .loanDate(entity.getLoanDate())
                .returnDate(entity.getReturnDate())
                .status(entity.getStatus())
                .build();
    }
}