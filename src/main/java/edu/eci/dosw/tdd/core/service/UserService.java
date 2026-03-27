package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.mapper.UserPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import edu.eci.dosw.tdd.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final UserPersistenceMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User addUser(User user) {
        userValidator.validateUserForCreation(user);
        if (user.getId() == null) {
            user.setId(IdGeneratorUtil.generateId());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity saved = userRepository.save(userMapper.toEntity(user));
        return userMapper.toDomain(saved);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDomain).toList();
    }

    public User getUserById(String id) {
        User user = userRepository.findById(id).map(userMapper::toDomain).orElse(null);
        userValidator.validateUserExists(user);
        return user;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDomain).orElse(null);
    }
}
