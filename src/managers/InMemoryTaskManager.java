package managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import exceptions.TaskValidationException;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = -1;
    protected final HashMap<Integer, TaskData> tasks = new HashMap<>();
    protected final HashMap<Integer, EpicData> epics = new HashMap<>();
    protected final HashMap<Integer, SubTaskData> subTasks = new HashMap<>();

    private final Comparator<TaskData> byStartTime = (TaskData t1, TaskData t2) -> {
        if (t1.getStartDate() == null && t2.getStartDate() == null) {
            return t1.getId() - t2.getId();
        }
        if (t1.getStartDate() == null) {
            return 1;
        }
        if (t2.getStartDate() == null) {
            return -1;
        }
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
                    taskData.getStartDate().isBefore(prioritizedTask.getEndTime()) && prioritizedTasks.size() > 1) {
                throw new TaskValidationException("Задача " + taskData.getName() + " пересекается с другой задачей по времени.");
            }

        }
        prioritizedTasks.add(taskData);

    }

    @Override
    public List<TaskData> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<SubTaskData> getEpicSubTasks(int id) {
        List<SubTaskData> result = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            if (epicId == id) {
                for (Integer subTaskId : epics.get(epicId).getSubTaskIdList()) {
                    ArrayList<SubTaskData> allSubTasks = getAllSubTasks();
                    for (SubTaskData subTask : allSubTasks) {
                        if (subTask.getId() == subTaskId) {
                            result.add(subTask);
                        }
                    }
                }
            }
        }
        return result;
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
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
            return tasks.remove(id);
        } else {
            return null;
        }
    }

    @Override
    public EpicData deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            inMemoryHistoryManager.remove(id);
            EpicData epic = epics.remove(id);

            for (Integer subTaskId : epic.getSubTaskIdList()) {
                inMemoryHistoryManager.remove(subTaskId);
                subTasks.remove(subTaskId);
                prioritizedTasks.remove(subTasks.get(subTaskId));
            }
            return epic;
        } else {
            return null;
        }
    }

    @Override
    public SubTaskData deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            inMemoryHistoryManager.remove(id);
            SubTaskData subTask = subTasks.remove(id);
            prioritizedTasks.remove(subTask);
            EpicData epic = epics.get(subTask.getEpicId());
            epic.removeSubTask(id);
            updateEpicTemporaryParams(epic);
            updateEpicStatus(epic.getId());
            return subTask;
        } else {
            return null;
        }
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
        if (savedEpic != null) {
            savedEpic.setName(epicData.getName());
            savedEpic.setDescription(epicData.getDescription());
        }
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
        LocalDateTime startDate = LocalDateTime.MAX;
        LocalDateTime endDate = LocalDateTime.MIN;
        long duration = 0;
        for (Integer subTaskId : epicData.getSubTaskIdList()) {
            SubTaskData subTask = subTasks.get(subTaskId);
            if (subTask.getStartDate() != null && subTask.getStartDate().isBefore(startDate)) {
                startDate = subTask.getStartDate();
            }
            if (subTask.getEndTime() != null && subTask.getEndTime().isAfter(endDate)) {
                endDate = subTask.getEndTime();
            }
            if (subTask.getDuration() != null) {
                duration += subTask.getDuration().toMinutes();
            }
        }
        if (!LocalDateTime.MAX.equals(startDate)) {
            epicData.setStartDate(startDate);
        } else {
            epicData.setStartDate(null);
        }
        if (!LocalDateTime.MIN.equals(endDate)) {
            epicData.setEndTime(endDate);
        } else {
            epicData.setEndTime(null);
        }
        epicData.setDuration(duration);
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
