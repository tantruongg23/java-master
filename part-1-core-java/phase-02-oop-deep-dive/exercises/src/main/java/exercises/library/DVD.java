package exercises.library;

public non-sealed class DVD extends LibraryItem implements Borrowable, Searchable {
    private String director;
    private int durationMinutes;

    protected DVD(String title, int year, String id) {
        super(title, year, id);
    }

    public DVD(String title, int year, String id, String director, int durationMinutes) {
        this(title, year, id);
        this.director = director;
        this.durationMinutes = durationMinutes;
    }
    @Override
    public String getCategory() {
        return getClass().getSimpleName();
    }


    @Override
    public void borrowItem(String patronId) {
        setCurrentBorrowerId(patronId);
        setAvailable(false);
    }

    @Override
    public void returnItem() {
        setCurrentBorrowerId(null);
        setAvailable(true);
    }

    @Override
    public boolean isAvailable() {
        return super.isAvailable();
    }

    @Override
    public boolean matchesKeyword(String keyword) {
        return getTitle().toLowerCase().contains(keyword.toLowerCase());
    }
}
