package exercises.library;

import java.util.List;

public class Patron {
    private String id;
    private String name;
    private List<Borrowable> borrowedItems;

    public Patron(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Borrowable> getBorrowedItems() {
        return borrowedItems;
    }

    public void setBorrowedItems(List<Borrowable> borrowedItems) {
        this.borrowedItems = borrowedItems;
    }

    public void borrowItem(Borrowable item) {
        if (!item.isAvailable()) {
            throw new IllegalStateException("Item is not available");
        }
        item.borrowItem(this.id);
        borrowedItems.add(item);
    }

    public void returnItem(Borrowable item) {
        item.returnItem();
        borrowedItems.remove(item);
    }

}
