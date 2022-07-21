package interfaces;

import dataClasses.TaskData;

import java.util.List;

public interface HistoryManager {
    void add(TaskData Task);

    List<TaskData> getHistory();
}
