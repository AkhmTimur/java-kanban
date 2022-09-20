package managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import exceptions.TaskValidationException;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = -1;
    protected final HashMap<Integer, TaskData> tasks = new HashMap<>();
    protected final HashMap<Integer, EpicData> epics = new HashMap<>();
    protected final HashMap<Integer, SubTaskData> subTasks = new HashMap<>();

    private final Comparator<TaskData> byStartTime = (TaskData t1, TaskData t2) -> {
        if(t1.getStartDate() == null && t2.getStartDate() == null) {
            return t1.getId() - t2.getId();
        }
        if(t1.getStartDate() == null) return 1;
        if(t2.getStartDate() == null) return -1;
        return t1.getStartDate().compareTo(t2.getStartDate());
    };

    private TreeSet<TaskData> prioritizedTasks = new TreeSet<>(byStartTime);
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
            updateEpicTemporaryParams(epicData);
        }
        updateEpicStatus(epicData.getId());
    }

    void addToSetValidation(TaskData taskData) {
        for (TaskData prioritizedTask : prioritizedTasks) {
            if (taskData.getEndTime().isAfter(prioritizedTask.getStartDate()) &&
                    taskData.getStartDate().isBefore(prioritizedTask.getEndTime())) {
                throw new TaskValidationException("Задача " + taskData.getName() + " пересекается с другой задачей по времени.");
            }

        }
        if (taskData.getStartDate() == null || taskData.getEndTime() == null) {
            /*prioritizedTasks.*/
        }
        prioritizedTasks.add(taskData);

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
        prioritizedTasks.remove(tasks.get(id));
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
            prioritizedTasks.remove(subTasks.get(subTaskId));
        }
        return epic;
    }

    @Override
    public SubTaskData deleteSubTaskById(int id) {
        inMemoryHistoryManager.remove(id);
        SubTaskData subTask = subTasks.remove(id);
        if (subTask == null) {
            return null;
        }
        prioritizedTasks.remove(subTask);
        EpicData epic = epics.get(subTask.getEpicId());
        epic.removeSubTask(id);
        updateEpicTemporaryParams(epic);
        updateEpicStatus(epic.getId());
        return subTask;
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
            updateEpicTemporaryParams(epicData);
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
        if (subTask != null) {
            subTasks.put(subTaskData.getId(), subTaskData);
            prioritizedTasks.remove(subTask);
            addToSetValidation(subTaskData);
            int epicId = subTaskData.getEpicId();
            updateEpicStatus(epicId);
            updateEpicTemporaryParams(epics.get(epicId));
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

    private void updateEpicTemporaryParams(EpicData epicData) {
        long newEpicDuration = 0;
        for (TaskData prioritizedTask : prioritizedTasks) {
            if (epicData.getSubTaskIdList().contains(prioritizedTask.getId())) {
                if (epicData.getStartDate() == null && prioritizedTask.getStartDate() != null) {
                    epicData.setStartDate(prioritizedTask.getStartDate());
                } else if (prioritizedTask.getStartDate().isBefore(epicData.getStartDate())) {
                    epicData.setStartDate(prioritizedTask.getStartDate());
                } else if (prioritizedTask.getEndTime().isAfter(epicData.getEndTime())) {
                    epicData.setEndTime(prioritizedTask.getEndTime());
                } else if (prioritizedTask.getEndTime().isBefore(epicData.getEndTime())) {
                    epicData.setEndTime(prioritizedTask.getEndTime());
                }
                if (epicData.getDuration() == null && prioritizedTask.getDuration() != null) {
                    epicData.setDuration(prioritizedTask.getDuration().toMinutes());
                } else {
                    newEpicDuration += prioritizedTask.getDuration().toMinutes();
                    epicData.setDuration(newEpicDuration);
                }
                if (epicData.getStartDate() != null && epicData.getEndTime() == null) {
                    epicData.setEndTime(epicData.getStartDate().plus(epicData.getDuration()));
                }
            }
        }
    }

    private int genID() {
        nextId++;
        return nextId;
    }

    @Override
    public List<TaskData> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}
