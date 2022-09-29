package managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;

import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    static String urlToKVServer;
    static Gson gson = new Gson();
    public KVTaskClient client;

    public static void main(String[] args){
        HTTPTaskManager httpTaskManager = Managers.getDefault();
        gson = new Gson();

        TaskData newTaskData = new TaskData("test`1", "Нужно тренироваться - едим бургеры!");
        TaskData newTaskData1 = new TaskData("test", "Попробовать свои силы на марафоне который будет осенью");

        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        newTaskData1.setDuration(240);
        newTaskData1.setStartDate(2022, 3, 24);

        httpTaskManager.addToTasks(newTaskData);
        httpTaskManager.addToTasks(newTaskData1);
    }

    public HTTPTaskManager(String urlToKVServer) {
        HTTPTaskManager.urlToKVServer = urlToKVServer;
        client = new KVTaskClient(urlToKVServer);
    }

    @Override
    public void save() {
        client.put("tasks", gson.toJson(getAllTasks()));
        client.put("epics", gson.toJson(getAllEpics()));
        client.put("subTasks", gson.toJson(getAllSubTasks()));
    }

    @Override
    public List<TaskData> getPrioritizedTasks() {
        loadFromServer();
        return super.getPrioritizedTasks();
    }

    @Override
    public List<TaskData> getHistory() {
        loadFromServer();
        return super.getHistory();
    }

    public void loadFromServer() {
        ArrayList<TaskData> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<TaskData>>() {}.getType());
        for (TaskData task : tasks) {
            this.addToTasks(task);
        }
        ArrayList<EpicData> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<EpicData>>() {}.getType());
        for (EpicData epic : epics) {
            this.addToEpics(epic);
        }
        ArrayList<SubTaskData> subTasks = gson.fromJson(client.load("subTasks"), new TypeToken<ArrayList<SubTaskData>>() {}.getType());
        for (SubTaskData subTask: subTasks) {
            this.addToSubTasks(subTask);
        }
    }
}
