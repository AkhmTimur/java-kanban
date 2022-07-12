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

        EpicData epicData = epics.get(subTaskData.getEpicId());
        if (epicData != null && "DONE".equals(epicData.getStatus())) {
            epicData.setStatus("IN_PROGRESS");
        }
        updateEpic(epicData);
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

    void deleteEpicById(int id) {
        EpicData epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subTaskId : epic.getSubTaskIdList()) {
            subTasks.remove(subTaskId);
        }
    }

    void deleteSubTaskById(int id) {
        SubTaskData subtask = subTasks.remove(id);
        if (subtask == null) {
            return;
        }
        EpicData epic = epics.get(subtask.getEpicId());
        epic.removeSubTask(Integer.valueOf(id));
        updateEpicStatus(epic.getId());
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
        subTasks.clear();
    }

    void deleteAllSubTasks() {
        subTasks.clear();
        for (EpicData epicData : epics.values()) {
            epicData.clearSubTaskIdList();
            epicData.setStatus("NEW");
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
        int epicId = subTaskData.getEpicId();
        updateEpicStatus(epicId);
    }

    void updateEpicStatus(int id) {
        ArrayList<String> subTasksStatuses = new ArrayList<>();
        ArrayList<String> uniqueStatuses = new ArrayList<>();

        for (SubTaskData subTask : subTasks.values()) {
            if (subTask.getEpicId() == id) {
                subTasksStatuses.add(subTask.status);
            }
        }

        for (String subTasksStatus : subTasksStatuses) {
            if(!uniqueStatuses.contains(subTasksStatus)) {
                uniqueStatuses.add(subTasksStatus);
            }
        }

        if (epics.get(id).getSubTaskIdList().size() == 0 ||
                (uniqueStatuses.size() == 1 && uniqueStatuses.get(0).equals("NEW"))) {
            epics.get(id).setStatus("NEW");
        } else if (uniqueStatuses.size() == 1 && uniqueStatuses.get(0).equals("DONE")) {
            epics.get(id).setStatus("DONE");
        } else {
            epics.get(id).setStatus("IN_PROGRESS");
        }
    }



    private int genID() {
        nextId++;
        return nextId;
    }
}
