package exercises.library;

public non-sealed class Book extends LibraryItem implements Borrowable, Searchable {
    private String author;
    private String isbn;
    private int pageCount;

    protected Book(String title, int year, String id) {
        super(title, year, id);
    }

    public Book(String title, int year, String id, String author, String isbn, int pageCount) {
        this(title, year, id);
        this.author = author;
        this.isbn = isbn;
        this.pageCount = pageCount;
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
