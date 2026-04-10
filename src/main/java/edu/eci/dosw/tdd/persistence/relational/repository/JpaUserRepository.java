package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
}
