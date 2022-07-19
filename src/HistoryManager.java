import java.util.List;

public interface HistoryManager {
    void add(TaskData Task);

    List<TaskData> getHistory();
}
