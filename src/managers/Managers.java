package managers;

import dataClasses.TaskData;
import interfaces.HistoryManager;
import interfaces.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager<TaskData> getHistoryDefault() {
        return new InMemoryHistoryManager();
    }
}
