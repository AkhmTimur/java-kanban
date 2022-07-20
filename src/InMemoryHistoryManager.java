import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<TaskData> history = new ArrayList<>(10);

    @Override
    public void add(TaskData data) {
        if (history.size() >= 10 ) {
            history.remove(0);
        }
        history.add(data);
    }

    @Override
    public List<TaskData> getHistory() {
        return history;
    }
}
