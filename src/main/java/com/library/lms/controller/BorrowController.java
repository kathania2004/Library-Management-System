package com.library.lms.controller;

import com.library.lms.entity.BorrowRecord;
import com.library.lms.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // POST http://localhost:8080/api/borrow?bookId=1&memberId=1
    @PostMapping
    public ResponseEntity<BorrowRecord> borrowBook(@RequestParam Long bookId, @RequestParam Long memberId) {
        BorrowRecord record = borrowService.borrowBook(bookId, memberId);
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    // PUT http://localhost:8080/api/borrow/return/5   (5 = the borrow record id)
    @PutMapping("/return/{recordId}")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long recordId) {
        return ResponseEntity.ok(borrowService.returnBook(recordId));
    }

    // GET http://localhost:8080/api/borrow/history/member/1
    @GetMapping("/history/member/{memberId}")
    public ResponseEntity<List<BorrowRecord>> getHistory(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowService.getBorrowHistoryForMember(memberId));
    }

    // GET http://localhost:8080/api/borrow/current/member/1
    @GetMapping("/current/member/{memberId}")
    public ResponseEntity<List<BorrowRecord>> getCurrentlyBorrowed(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowService.getCurrentlyBorrowedByMember(memberId));
    }

    // GET http://localhost:8080/api/borrow/overdue
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecord>> getOverdueBooks() {
        return ResponseEntity.ok(borrowService.getOverdueBooks());
    }
}
