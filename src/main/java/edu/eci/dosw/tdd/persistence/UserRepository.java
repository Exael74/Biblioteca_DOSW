package edu.eci.dosw.tdd.persistence;

import edu.eci.dosw.tdd.core.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    boolean existsById(String id);

    boolean existsByUsername(String username);

    void deleteById(String id);
}
