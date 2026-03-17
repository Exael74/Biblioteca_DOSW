package edu.eci.dosw.tdd.validator;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.exception.UserNotFoundException;
import edu.eci.dosw.tdd.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateUserForCreation(User user) {
        ValidationUtil.requireNotNull(user, "User cannot be null");
        ValidationUtil.requireNonEmpty(user.getName(), "User name cannot be empty");
    }

    public void validateUserExists(User user) {
        if (user == null) {
            throw new UserNotFoundException("User not found in the system");
        }
    }
}
