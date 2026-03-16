package exercises.library;

import java.util.Objects;

/**
 * LibraryItem — abstract base class for all items in the library catalog.
 *
 * <p>TODO: Convert this to a sealed class:
 * <pre>
 *   public abstract sealed class LibraryItem permits Book, DVD, Magazine { ... }
 * </pre>
 * This restricts the type hierarchy — only the permitted subclasses can extend it.
 *
 * <p>TODO: Create the following subclasses in separate files:
 * <ul>
 *   <li>{@code Book} — additional fields: author, isbn, pageCount.
 *       Implements {@code Borrowable} and {@code Searchable}.</li>
 *   <li>{@code DVD} — additional fields: director, durationMinutes.
 *       Implements {@code Borrowable} and {@code Searchable}.</li>
 *   <li>{@code Magazine} — additional fields: issueNumber, publisher.
 *       Implements only {@code Searchable} (reference-only, not borrowable).</li>
 * </ul>
 *
 * <p>TODO: Create the following interfaces:
 * <ul>
 *   <li>{@code Borrowable} — borrowItem(String patronId), returnItem(), isAvailable()</li>
 *   <li>{@code Searchable} — default method matchesKeyword(String keyword) that checks title</li>
 * </ul>
 *
 * <p>TODO: Create a {@code Patron} class with: name, id, List of borrowed Borrowable items.
 *
 * <p>BONUS: Create a {@code BorrowingRecord} as a record:
 * <pre>
 *   record BorrowingRecord(String patronId, LocalDate borrowDate, LocalDate dueDate) {}
 * </pre>
 *
 * <p>BONUS: Implement an Observer pattern with {@code OverdueNotifier}.
 */
public abstract sealed class LibraryItem permits Book, DVD, Magazine{

    private final String title;
    private final int year;
    private final String id;
    private String currentBorrowerId;
    private boolean available;

    protected LibraryItem(String title, int year, String id) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.year = year;
        this.id = Objects.requireNonNull(id, "id must not be null");
    }

    /**
     * @return the category label for this item (e.g., "Fiction", "Documentary", "Periodical")
     */
    public abstract String getCategory();

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    public String getCurrentBorrowerId() {
        return currentBorrowerId;
    }

    public void setCurrentBorrowerId(String currentBorrowerId) {
        this.currentBorrowerId = currentBorrowerId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LibraryItem other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", id='" + id + '\'' +
                '}';
    }
}
