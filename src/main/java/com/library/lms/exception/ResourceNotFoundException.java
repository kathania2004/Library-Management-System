package com.library.lms.exception;

/**
 * Thrown when someone asks for a book/member/record by an id that
 * doesn't exist in the database. We catch this in GlobalExceptionHandler
 * and turn it into a clean 404 response instead of a scary stack trace.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
