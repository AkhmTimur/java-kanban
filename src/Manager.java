import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Manager {
    private HashMap<Integer, TaskData> tasks = new HashMap<>();
    private HashMap<Integer, EpicData> epics = new HashMap<>();

    HashMap<Integer, TaskData> getTasks() {
        return tasks;
    }

    HashMap<Integer, EpicData> getEpics() {
        return epics;
    }

    void addToTasks(TaskData taskData) {
        if(!tasks.containsKey(taskData.id)) {
            tasks.put(taskData.id, taskData);
        }
    }

    void addToEpics(EpicData epicData) {
        if(!epics.containsKey(epicData.id)) {
            epics.put(epicData.id, epicData);
        }
    }

    void addSubTaskToEpics(SubTaskData subTaskData) {
        epics.get(subTaskData.getEpicId()).addSubTask(subTaskData);
        updateSubTask(subTaskData);
    }

    ArrayList<TaskData> getAllTasks() {
        ArrayList<TaskData> result = new ArrayList<>();
        System.out.println("Список задач: ");
        for (Integer k : tasks.keySet()) {
            result.add(tasks.get(k));
        }
        return result;
    }

    ArrayList<EpicData> getAllEpics() {
        ArrayList<EpicData> result = new ArrayList<>();
        System.out.println("Список эпиков: ");
        for (Integer k : epics.keySet()) {
            System.out.println(k);
            result.add(epics.get(k));
        }
        return result;
    }

    void deleteAllTasks() {
        tasks.clear();
    }

    void deleteAllEpics() {
        epics.clear();
    }

    void deleteAllSubTasks() {
        for (Integer k : epics.keySet()) {
            epics.get(k).subTasks.clear();
        }
    }

    ArrayList<HashMap<Integer, SubTaskData>> getAllSubTasks() {
        ArrayList<HashMap<Integer, SubTaskData>> result = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            result.add(epics.get(id).subTasks);
        }
        return result;
    }

    void updateTask(TaskData taskData) {
        tasks.put(taskData.id, taskData);
    }

    void updateEpics(EpicData epicData) {
        epics.put(epicData.id, epicData);
    }

    void updateSubTask(SubTaskData subTaskData) {
        epics.get(subTaskData.getEpicId()).subTasks.put(subTaskData.id, subTaskData);
        isEpicDone(subTaskData.getEpicId());
    }

    TaskData getTaskById(int id) {
        return tasks.get(id);
    }

    EpicData getEpicById(int id) {
        return epics.get(id);
    }

    ArrayList<SubTaskData> getSubTaskById(int id) {
        ArrayList<SubTaskData> result = new ArrayList<>();
        for (Integer k : epics.keySet()) {
            result.add(epics.get(k).subTasks.get(id));
        }
        return result;
    }

    private boolean isEpicDone(Integer epicId) {
        boolean result = false;
        for (Integer id : epics.get(epicId).subTasks.keySet()) {
            if(epics.get(epicId).subTasks.get(id).status.equals("DONE")) {
                result = true;
            } else {
                return false;
            }
        }
        return result;
    }

    int genID(String name) {
        int id = 17;
        if (name != null) {
            id = id + name.hashCode();
        }
        id = id * 31;

        return id;
    }
}
