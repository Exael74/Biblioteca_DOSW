package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.exception.UserNotFoundException;
import edu.eci.dosw.tdd.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    
    // In-Memory Database
    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    public User addUser(User user) {
        userValidator.validateUserForCreation(user);
        if (user.getId() == null) {
            user.setId(IdGeneratorUtil.generateId());
        }
        userMap.put(user.getId(), user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User getUserById(String id) {
        User user = userMap.get(id);
        userValidator.validateUserExists(user);
        return user;
    }
}
