package managers;

import dataClasses.TaskData;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.io.IOException;

public class Managers {

    public static HTTPTaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:" + KVServer.PORT);
    }

    public static HistoryManager<TaskData> getHistoryDefault() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBackedTasksManager() throws IOException {
        return new FileBackedTasksManager();
    }
}
