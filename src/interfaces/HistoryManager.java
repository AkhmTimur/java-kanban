package interfaces;

import dataClasses.TaskData;
import java.util.ArrayList;
import java.util.Set;

public interface HistoryManager<T> {
    void add(T Task);

    ArrayList<TaskData> getHistory();

    void remove(int id);

    void removeDataType(Set<Integer> dataId);
}
