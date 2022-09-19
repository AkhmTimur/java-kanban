package managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import exceptions.TaskValidationException;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = -1;
    protected final HashMap<Integer, TaskData> tasks = new HashMap<>();
    protected final HashMap<Integer, EpicData> epics = new HashMap<>();
    protected final HashMap<Integer, SubTaskData> subTasks = new HashMap<>();

    Comparator<TaskData> byStartTime = (TaskData t1, TaskData t2) -> {
        return t1.getStartDate().compareTo(t2.getStartDate());
    };

    TreeSet<TaskData> prioritizedTasks = new TreeSet<>(byStartTime);
    protected HistoryManager<TaskData> inMemoryHistoryManager = Managers.getHistoryDefault();

    @Override
    public void addToTasks(TaskData taskData) {
        taskData.setId(genID());
        addToSetValidation(taskData);
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
        if (subTaskData.getStartDate() != null) {
            addToSetValidation(subTaskData);
            updateEpicTemporaryParams(epicData, subTaskData);
        }
        updateEpicStatus(epicData.getId());
    }

    void addToSetValidation(TaskData taskData) {
        TaskData taskToAdd = null;
        for (TaskData prioritizedTask : prioritizedTasks) {
            if(taskData.getEndTime().isBefore(prioritizedTask.getStartDate()) ||
                    taskData.getStartDate().isAfter(prioritizedTask.getEndTime())) {
                taskToAdd = taskData;
            } else {
                throw new TaskValidationException("Задача " + taskData.getName() + " пересекается с другой задачей по времени.");
            }
        }
        if(taskToAdd != null) {
            prioritizedTasks.add(taskToAdd);
        }
    }

    @Override
    public List<TaskData> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        prioritizedTasks.remove(getTaskById(id));
        return tasks.remove(id);
    }

    @Override
    public EpicData deleteEpicById(int id) {
        inMemoryHistoryManager.remove(id);
        EpicData epic = epics.remove(id);
        if (epic == null) {
            return null;
        }
        for (Integer subTaskId : epic.getSubTaskIdList()) {
            inMemoryHistoryManager.remove(subTaskId);
            subTasks.remove(subTaskId);
            prioritizedTasks.remove(getSubTaskById(subTaskId));
        }
        return epic;
    }

    @Override
    public SubTaskData deleteSubTaskById(int id) {
        inMemoryHistoryManager.remove(id);
        prioritizedTasks.remove(getSubTaskById(id));
        SubTaskData subTask = subTasks.remove(id);
        if (subTask == null) {
            return null;
        }
        EpicData epic = epics.get(subTask.getEpicId());
        epic.removeSubTask(id);
        updateEpicStatus(epic.getId());
        return subTasks.remove(id);
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
        inMemoryHistoryManager.removeDataType(tasks.keySet());
        for (TaskData task : tasks.values()) {
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        inMemoryHistoryManager.removeDataType(epics.keySet());
        for (EpicData epic : epics.values()) {
            prioritizedTasks.remove(epic);
        }
        epics.clear();
        for (SubTaskData subTask : subTasks.values()) {
            prioritizedTasks.remove(subTask);
        }
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        inMemoryHistoryManager.removeDataType(subTasks.keySet());
        for (SubTaskData subTask : subTasks.values()) {
            prioritizedTasks.remove(subTask);
        }
        subTasks.clear();
        for (EpicData epicData : epics.values()) {
            epicData.clearSubTaskIdList();
            epicData.setStatus(Statuses.NEW);
        }
    }

    @Override
    public void updateTask(TaskData taskData) {
        TaskData task = tasks.get(taskData.getId());
        if (task != null) {
            tasks.put(taskData.getId(), taskData);
            prioritizedTasks.remove(task);
            addToSetValidation(taskData);
        }
    }

    @Override
    public void updateEpic(EpicData epicData) {
        EpicData savedEpic = epics.get(epicData.getId());
        savedEpic.setName(epicData.getName());
        savedEpic.setDescription(epicData.getDescription());
    }

    @Override
    public void updateSubTask(SubTaskData subTaskData) {
        SubTaskData subTask = subTasks.get(subTaskData.getId());
        if(subTask != null) {
            subTasks.put(subTaskData.getId(), subTaskData);
            prioritizedTasks.remove(subTask);
            addToSetValidation(subTaskData);
            int epicId = subTaskData.getEpicId();
            updateEpicStatus(epicId);
        }
    }

    public void updateEpicStatus(int id) {
        Set<Statuses> uniqueStatuses = new HashSet<>();
        EpicData epicData = epics.get(id);
        for (Integer subTaskId : epicData.getSubTaskIdList()) {
            uniqueStatuses.add(subTasks.get(subTaskId).getStatus());
        }
        if (epicData.getSubTaskIdList().size() == 0 ||
                (uniqueStatuses.size() == 1 && uniqueStatuses.contains(Statuses.NEW))) {
            epicData.setStatus(Statuses.NEW);
        } else if (uniqueStatuses.size() == 1 && uniqueStatuses.contains(Statuses.DONE)) {
            epicData.setStatus(Statuses.DONE);
        } else {
            epicData.setStatus(Statuses.IN_PROGRESS);
        }
    }

    private void updateEpicTemporaryParams(EpicData epicData, SubTaskData subTaskData) {
        if(epicData.getStartDate() == null && subTaskData.getStartDate() != null) {
            epicData.setStartDate(subTaskData.getStartDate());
        } else if (subTaskData.getStartDate().isBefore(epicData.getStartDate())) {
            epicData.setStartDate(subTaskData.getStartDate());
        }  else if (subTaskData.getEndTime().isAfter(epicData.getEndTime())) {
            epicData.setEndTime(subTaskData.getEndTime());
        }
        if(epicData.getDuration() == null && subTaskData.getDuration() != null) {
            epicData.setDuration(subTaskData.getDuration().toMinutes());
        } else {
            long epicDuration = epicData.getDuration().toMinutes();
            epicDuration += subTaskData.getDuration().toMinutes();
            epicData.setDuration(epicDuration);
        }
    }

    private long calcEpicDuration(EpicData epic) {
        long result = 0;
        for (Integer subTaskId : epic.getSubTaskIdList()) {
            result += getSubTaskById(subTaskId).getDuration().toMinutes();
        }
        return result;
    }

    private int genID() {
        nextId++;
        return nextId;
    }

    public List<Integer> getHistory() {
        return null;
    }
}
