package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, String> {
	List<BookEntity> findByAvailableCopiesGreaterThan(int copies);
}
