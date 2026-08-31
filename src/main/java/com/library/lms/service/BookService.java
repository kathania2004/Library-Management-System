package com.library.lms.service;

import com.library.lms.entity.Book;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The @Service layer sits between the Controller (which handles HTTP)
 * and the Repository (which handles the database). This is where business
 * rules belong — controllers should stay "thin" and just delegate here.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;

    // Constructor injection: Spring automatically supplies a BookRepository
    // bean here. This is the recommended way to inject dependencies
    // (preferred over @Autowired on fields).
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    public Book updateBook(Long id, Book updatedBook) {
        Book existing = getBookById(id); // reuses the not-found check above

        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setIsbn(updatedBook.getIsbn());

        // If total copies increases, add the difference to available copies too.
        int diff = updatedBook.getTotalCopies() - existing.getTotalCopies();
        existing.setTotalCopies(updatedBook.getTotalCopies());
        existing.setAvailableCopies(existing.getAvailableCopies() + diff);

        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        Book existing = getBookById(id);
        bookRepository.delete(existing);
    }
}
