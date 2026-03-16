package exercises.library;

public non-sealed class Magazine extends LibraryItem implements Searchable {
    private String publisher;
    private int issueNumber;

    protected Magazine(String title, int year, String id) {
        super(title, year, id);
    }

    public Magazine(String title, int year, String id, String publisher, int issueNumber) {
        this(title, year, id);
        this.publisher = publisher;
        this.issueNumber = issueNumber;
    }
    @Override
    public String getCategory() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean matchesKeyword(String keyword) {
        return getTitle().toLowerCase().contains(keyword.toLowerCase());
    }
}
