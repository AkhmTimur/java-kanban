package interfaces;

import dataClasses.TaskData;
import managers.Node;

import java.util.ArrayList;

public interface HistoryManager<T> {
    void add(TaskData Task);

    void removeNode(Node<T> node);

    ArrayList<TaskData> getHistory();
}
