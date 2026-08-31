package com.library.lms.repository;

import com.library.lms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * By extending JpaRepository<Book, Long> we get save(), findById(),
 * findAll(), deleteById(), etc. for FREE — no SQL, no implementation
 * needed. Spring generates the implementation at runtime.
 *
 * The method below is a "query method" — Spring reads the method name
 * and builds the SQL query automatically:
 *   findByTitleContainingIgnoreCase("harry")
 *     -> SELECT * FROM books WHERE LOWER(title) LIKE LOWER('%harry%')
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);
}
