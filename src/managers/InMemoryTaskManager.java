package managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = -1;
    HashMap<Integer, TaskData> tasks = new HashMap<>();
    HashMap<Integer, EpicData> epics = new HashMap<>();
    HashMap<Integer, SubTaskData> subTasks = new HashMap<>();

    Comparator<TaskData> byStartTime = (TaskData t1, TaskData t2) -> {
        int result1 = t1.getStartDate().compareTo(t2.getStartDate());
        int result2 =  t2.getStartDate().compareTo(t1.getEndTime());
        return result1 * result2;
    };

    TreeSet<TaskData> prioritizedTasks = new TreeSet<>(byStartTime);
    protected HistoryManager<TaskData> inMemoryHistoryManager = Managers.getHistoryDefault();

    @Override
    public void addToTasks(TaskData taskData) {
        taskData.setId(genID());
        addToSetAndValidation(taskData);
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
            addToSetAndValidation(subTaskData);
            prioritizedTasks.add(subTaskData);
            int year = subTaskData.getStartDate().getYear();
            int month = subTaskData.getStartDate().getMonthValue();
            int day = subTaskData.getStartDate().getDayOfMonth();
            if (epicData.getStartDate() != null && epicData.getDuration() != null) {
                if (subTaskData.getStartDate().isBefore(epicData.getStartDate())) {
                    epicData.setStartDate(year, month, day);
                } else if (subTaskData.getEndTime().isAfter(epicData.getEndTime())) {
                    epicData.setDuration(Duration.between(epicData.getStartDate(), subTaskData.getEndTime()).toMinutes());
                }
            } else {
                epicData.setStartDate(year, month, day);
                epicData.setDuration(Duration.between(subTaskData.getStartDate(), subTaskData.getEndTime()).toMinutes());
            }
        }

        updateEpicStatus(epicData.getId());
        updateEpic(epicData);
    }

    protected void addSubTaskToEpics(SubTaskData subTaskData) {
        epics.get(subTaskData.getEpicId()).addSubTask(subTaskData);
    }

    void addToSetAndValidation(TaskData taskData) {
        if(taskData.getStartDate() != null) {
            prioritizedTasks.add(taskData);
            if(!prioritizedTasks.contains(taskData)) {
                System.out.println("Задача " + taskData.getName() + " пересекается с другой задачей по времени.");
            }
        }
    }

    Set<TaskData> getPrioritizedTasks() {
        return prioritizedTasks;
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
    public EpicData deleteEpicById(int id) {
        inMemoryHistoryManager.remove(id);
        EpicData epic = epics.remove(id);
        if (epic == null) {
            return null;
        }
        for (Integer subTaskId : epic.getSubTaskIdList()) {
            inMemoryHistoryManager.remove(subTaskId);
            subTasks.remove(subTaskId);
        }
        return epics.remove(id);
    }

    @Override
    public SubTaskData deleteSubTaskById(int id) {
        inMemoryHistoryManager.remove(id);
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

    void updateEpicStatus(int id) {
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


    int genID() {
        nextId++;
        return nextId;
    }
}
