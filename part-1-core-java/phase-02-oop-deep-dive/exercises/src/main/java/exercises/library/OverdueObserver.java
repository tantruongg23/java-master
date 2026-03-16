package exercises.library;

public interface OverdueObserver {
    void onItemOverdue(BorrowingRecord borrowingRecord);
}
