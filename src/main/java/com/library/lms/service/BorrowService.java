package com.library.lms.service;

import com.library.lms.entity.Book;
import com.library.lms.entity.BorrowRecord;
import com.library.lms.entity.BorrowStatus;
import com.library.lms.entity.Member;
import com.library.lms.exception.BookNotAvailableException;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.BookRepository;
import com.library.lms.repository.BorrowRecordRepository;
import com.library.lms.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BorrowService {

    // Business rules — in a bigger app these would come from application.properties
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 10.0; // e.g. ₹10 per day late

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                          BookRepository bookRepository,
                          MemberRepository memberRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * A member borrows a book.
     * Steps: find the book, find the member, check a copy is available,
     * reduce availableCopies by 1, and create a BorrowRecord.
     */
    public BorrowRecord borrowBook(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies of '" + book.getTitle() + "' are currently available");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        BorrowRecord record = new BorrowRecord(book, member, today, today.plusDays(LOAN_PERIOD_DAYS));
        return borrowRecordRepository.save(record);
    }

    /**
     * A member returns a book.
     * Steps: find the (still-open) borrow record, mark it returned,
     * calculate a fine if it's late, and give the copy back to the book's stock.
     */
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id: " + recordId));

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new BookNotAvailableException("This book has already been returned");
        }

        LocalDate today = LocalDate.now();
        record.setReturnDate(today);
        record.setStatus(BorrowStatus.RETURNED);

        // Fine calculation: only charged if returned after the due date
        if (today.isAfter(record.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), today);
            record.setFineAmount(daysLate * FINE_PER_DAY);
        }

        // Give the physical copy back to the shelf
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    public List<BorrowRecord> getBorrowHistoryForMember(Long memberId) {
        return borrowRecordRepository.findByMemberId(memberId);
    }

    public List<BorrowRecord> getCurrentlyBorrowedByMember(Long memberId) {
        return borrowRecordRepository.findByMemberIdAndStatus(memberId, BorrowStatus.BORROWED);
    }

    public List<BorrowRecord> getOverdueBooks() {
        return borrowRecordRepository.findByStatusAndDueDateBefore(BorrowStatus.BORROWED, LocalDate.now());
    }
}
