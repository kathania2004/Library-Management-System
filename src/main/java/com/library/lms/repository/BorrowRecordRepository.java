package com.library.lms.repository;

import com.library.lms.entity.BorrowRecord;
import com.library.lms.entity.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // All records (past and present) for one member
    List<BorrowRecord> findByMemberId(Long memberId);

    // All currently borrowed (not yet returned) books for one member
    List<BorrowRecord> findByMemberIdAndStatus(Long memberId, BorrowStatus status);

    // Every book that's overdue and still not returned — useful for a "send reminder" feature
    List<BorrowRecord> findByStatusAndDueDateBefore(BorrowStatus status, LocalDate date);
}
