package managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;

import java.util.ArrayList;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final Gson gson = new Gson();
    public KVTaskClient client;
    private boolean readyToLoad = false;

    public static void main(String[] args){
        HTTPTaskManager httpTaskManager = Managers.getDefault();

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
        client = new KVTaskClient(urlToKVServer);
        if(readyToLoad) {
            loadFromServer();
        }
    }

    @Override
    public void save() {
        if(getAllTasks().size() > 0 || getAllEpics().size() > 0 || getAllSubTasks().size() > 0 || getHistory().size() > 0) {
            client.put("tasks", gson.toJson(getAllTasks()));
            client.put("epics", gson.toJson(getAllEpics()));
            client.put("subTasks", gson.toJson(getAllSubTasks()));
            client.put("history", gson.toJson(getHistory()));
            readyToLoad = true;
        }
    }

    public void loadFromServer() {
        ArrayList<TaskData> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<TaskData>>() {}.getType());
        if(tasks.size() > 0 ) {
            for (TaskData task : tasks) {
                if(task.getId() > nextId) {
                    nextId = task.getId();
                }
                this.tasks.put(task.getId(), task);
            }
        }
        ArrayList<EpicData> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<EpicData>>() {}.getType());
        if(epics.size() > 0) {
            for (EpicData epic : epics) {
                if(epic.getId() > nextId) {
                    nextId = epic.getId();
                }
                this.epics.put(epic.getId(), epic);
            }
        }
        ArrayList<SubTaskData> subTasks = gson.fromJson(client.load("subTasks"), new TypeToken<ArrayList<SubTaskData>>() {}.getType());
        if(subTasks.size() > 0) {
            for (SubTaskData subTask: subTasks) {
                if(subTask.getId() > nextId) {
                    nextId = subTask.getId();
                }
                this.subTasks.put(subTask.getId(), subTask);
            }
        }
        ArrayList<TaskData> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<TaskData>>() {}.getType());
        if(history.size() > 0) {
            for (TaskData taskData : history) {
                inMemoryHistoryManager.add(taskData);
            }
        }

    }
}
