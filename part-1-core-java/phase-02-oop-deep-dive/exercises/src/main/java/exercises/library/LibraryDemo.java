package exercises.library;

import java.util.List;

public class LibraryDemo {
    public static void main(String[] args) {
        // 1. Create items
        Book book = new Book("Clean Code", 2008, "B001", "Robert C. Martin", "978-0132350884", 464);
        DVD dvd = new DVD("The Matrix", 1999, "D001", "Wachowskis", 136);
        Magazine mag = new Magazine("National Geographic", 2024, "M001", "Nat Geo", 202);

        // 2. Create patron
        Patron patron = new Patron("P001", "Alice");

        // 3. Borrow items
        patron.borrowItem(book);
        patron.borrowItem(dvd);

        // 4. Try to borrow magazine (should fail - not borrowable!)
        // patron.borrowItem(mag); // Compile error!

        // 5. Search
        System.out.println(book.matchesKeyword("clean")); // true

        // 6. Show polymorphism
        List<LibraryItem> catalog = List.of(book, dvd, mag);
        for (LibraryItem item : catalog) {
            System.out.println(item.getCategory() + ": " + item.getTitle());
        }

        // 7. Demonstrate sealed class (this should not compile)
        // class Newspaper extends LibraryItem { } // Compile error!
    }
}
