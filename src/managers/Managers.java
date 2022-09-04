package managers;

import dataClasses.TaskData;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager<TaskData> getHistoryDefault() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBackedTasksManager() throws IOException {
        return new FileBackedTasksManager();
    }
}
