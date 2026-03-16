package exercises.library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OverdueNotifier {
    private final List<OverdueObserver> observers = new ArrayList<>();

    public void addObserver(OverdueObserver observer) {
        observers.add(observer);
    }

    public void checkOverdueItems(List<BorrowingRecord> records) {
        LocalDate today = LocalDate.now();
        for (BorrowingRecord borrowingRecord : records) {
            if (today.isAfter(borrowingRecord.dueDate())) {
                notifyObservers(borrowingRecord);
            }
        }
    }

    private void notifyObservers(BorrowingRecord borrowingRecord) {
        for (OverdueObserver observer : observers) {
            observer.onItemOverdue(borrowingRecord);
        }
    }
}