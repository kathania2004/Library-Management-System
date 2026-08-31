package com.library.lms.entity;

/**
 * A simple enum instead of storing "BORROWED"/"RETURNED" as raw strings
 * everywhere. This avoids typos like "borowed" breaking your queries.
 */
public enum BorrowStatus {
    BORROWED,
    RETURNED
}
