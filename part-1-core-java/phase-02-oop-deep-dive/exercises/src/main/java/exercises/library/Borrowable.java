package exercises.library;

public interface Borrowable {
    void borrowItem(String patronId);

    void returnItem();

    boolean isAvailable();
}
