package exercises.library;

import java.time.LocalDate;

public record BorrowingRecord(String patronId, String itemId, LocalDate borrowDate, LocalDate dueDate) { }