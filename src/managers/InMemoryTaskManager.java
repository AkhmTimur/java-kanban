package managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = -1;
    private HashMap<Integer, TaskData> tasks = new HashMap<>();
    private HashMap<Integer, EpicData> epics = new HashMap<>();
    private HashMap<Integer, SubTaskData> subTasks = new HashMap<>();
    private HistoryManager<TaskData> inMemoryHistoryManager = Managers.getHistoryDefault();

    @Override
    public void addToTasks(TaskData taskData) {
        taskData.setId(genID());
        tasks.put(taskData.getId(), taskData);
    }

    @Override
    public int addToEpics(EpicData epicData) {
        int epicID = genID();
        epicData.setId(epicID);
        epics.put(epicData.getId(), epicData);
        return epicID;
    }

    @Override
    public void addToSubTasks(SubTaskData subTaskData) {
        subTaskData.setId(genID());
        subTasks.put(subTaskData.getId(), subTaskData);
        EpicData epicData = epics.get(subTaskData.getEpicId());
        epicData.addSubTask(subTaskData);
        updateEpicStatus(epicData.getId());
        updateEpic(epicData);
    }

    protected void addSubTaskToEpics(SubTaskData subTaskData) {
        epics.get(subTaskData.getEpicId()).addSubTask(subTaskData);
    }

    @Override
    public TaskData getTaskById(int id) {
        inMemoryHistoryManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public EpicData getEpicById(int id) {
        inMemoryHistoryManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTaskData getSubTaskById(int id) {
        inMemoryHistoryManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public TaskData deleteTaskById(int id) {
        inMemoryHistoryManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        inMemoryHistoryManager.remove(id);
        EpicData epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subTaskId : epic.getSubTaskIdList()) {
            inMemoryHistoryManager.remove(subTaskId);
            subTasks.remove(subTaskId);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        inMemoryHistoryManager.remove(id);
        SubTaskData subTask = subTasks.remove(id);
        if (subTask == null) {
            return;
        }
        EpicData epic = epics.get(subTask.getEpicId());
        epic.removeSubTask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public ArrayList<TaskData> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<EpicData> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTaskData> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        inMemoryHistoryManager.removeDataType(subTasks.keySet());
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        inMemoryHistoryManager.removeDataType(epics.keySet());
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        inMemoryHistoryManager.removeDataType(subTasks.keySet());
        subTasks.clear();
        for (EpicData epicData : epics.values()) {
            epicData.clearSubTaskIdList();
            epicData.setStatus(Statuses.NEW);
        }
    }

    @Override
    public void updateTask(TaskData taskData) {
        tasks.put(taskData.getId(), taskData);
    }

    @Override
    public void updateEpic(EpicData epicData) {
        epics.put(epicData.getId(), epicData);
    }

    @Override
    public void updateSubTask(SubTaskData subTaskData) {
        subTasks.put(subTaskData.getId(), subTaskData);
        int epicId = subTaskData.getEpicId();
        updateEpicStatus(epicId);
    }

    private void updateEpicStatus(int id) {
        ArrayList<Statuses> subTasksStatuses = new ArrayList<>();
        ArrayList<Statuses> uniqueStatuses = new ArrayList<>();

        for (SubTaskData subTask : subTasks.values()) {
            if (subTask.getEpicId() == id) {
                subTasksStatuses.add(subTask.status);
            }
        }

        for (Statuses subTasksStatus : subTasksStatuses) {
            if (!uniqueStatuses.contains(subTasksStatus)) {
                uniqueStatuses.add(subTasksStatus);
            }
        }

        if (epics.get(id).getSubTaskIdList().size() == 0 ||
                (uniqueStatuses.size() == 1 && uniqueStatuses.get(0) == Statuses.NEW)) {
            epics.get(id).setStatus(Statuses.NEW);
        } else if (uniqueStatuses.size() == 1 && uniqueStatuses.get(0) == Statuses.DONE) {
            epics.get(id).setStatus(Statuses.DONE);
        } else {
            epics.get(id).setStatus(Statuses.IN_PROGRESS);
        }
    }

    @Override
    public List<TaskData> getHistory() {
        return null;
    }

    private int genID() {
        nextId++;
        return nextId;
    }
}
