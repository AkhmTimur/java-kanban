import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    static int nextId = 0;
    private HashMap<Integer, TaskData> tasks = new HashMap<>();
    private HashMap<Integer, EpicData> epics = new HashMap<>();
    private HashMap<Integer, SubTaskData> subTasks = new HashMap<>();

    void addToTasks(TaskData taskData) {
        taskData.setId(genID());
        tasks.put(taskData.getId(), taskData);
    }

    void addToEpics(EpicData epicData) {
        epicData.setId(genID());
        epics.put(epicData.getId(), epicData);
    }

    void addToSubTasks(SubTaskData subTaskData) {
        subTaskData.setId(genID());
        subTasks.put(subTaskData.getId(), subTaskData);
        addSubTaskToEpics(subTaskData);
        if(epics.get(subTaskData.getEpicId()).status.equals("DONE")) {
            EpicData epicData = new EpicData(epics.get(subTaskData.getEpicId()).name, epics.get(subTaskData.getEpicId()).description, "IN PROGRESS");
            updateEpic(epicData);
        }
    }

    private void addSubTaskToEpics(SubTaskData subTaskData) {
        epics.get(subTaskData.getEpicId()).addSubTask(subTaskData);
    }

    TaskData getTaskById(int id) {
        return tasks.get(id);
    }

    EpicData getEpicById(int id) {
        return epics.get(id);
    }

    SubTaskData getSubTaskById(int id) {
        return subTasks.get(id);
    }

    TaskData deleteTaskById(int id) {
        return tasks.remove(id);
    }

    EpicData deleteEpicById(int id) {
        return epics.remove(id);
    }

    SubTaskData deleteSubTaskById(int id) {
        return subTasks.remove(id);
    }

    ArrayList<TaskData> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    ArrayList<EpicData> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    ArrayList<SubTaskData> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    void deleteAllTasks() {
        tasks.clear();
    }

    void deleteAllEpics() {
        epics.clear();
        deleteAllSubTasks();
    }

    void deleteAllSubTasks() {
        subTasks.clear();
        for (Integer k : epics.keySet()) {
            epics.get(k).getSubTaskIdList().clear();
            EpicData epicData = new EpicData(epics.get(k).name, epics.get(k).description, "NEW");
            updateEpic(epicData);
        }

    }

    void updateTask(TaskData taskData) {
        tasks.put(taskData.getId(), taskData);
    }

    void updateEpic(EpicData epicData) {
        epics.put(epicData.getId(), epicData);
    }

    void updateSubTask(SubTaskData subTaskData) {
        subTasks.put(subTaskData.getId(), subTaskData);

        if(epics.get(subTaskData.getEpicId()).status.equals("DONE")) {
            EpicData epicData = new EpicData(epics.get(subTaskData.getEpicId()).name, epics.get(subTaskData.getEpicId()).description, "IN PROGRESS");
            updateEpic(epicData);
        }

        isEpicDone(subTaskData.getEpicId());
    }

    private boolean isEpicDone(Integer epicId) {
        boolean result = false;
        for (Integer id : subTasks.keySet()) {
            if(subTasks.get(id).getEpicId() == epicId && subTasks.get(id).status.equals("DONE")) {
                result = true;
            } else {
                return false;
            }
        }
        return result;
    }

    int genID() {
        nextId++;
        return nextId;
    }
}
