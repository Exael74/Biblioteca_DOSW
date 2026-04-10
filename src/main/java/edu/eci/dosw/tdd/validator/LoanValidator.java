package edu.eci.dosw.tdd.validator;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class LoanValidator {

    private static final int MAX_LOANS_PER_USER = 3;

    public void validateLoanCreation(User user, long activeLoans) {
        ValidationUtil.requireNotNull(user, "User cannot be null");

        if (activeLoans >= MAX_LOANS_PER_USER) {
            throw new LoanLimitExceededException("User has reached the maximum number of active loans (" + MAX_LOANS_PER_USER + ")");
        }
    }
}