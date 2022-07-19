import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    static int nextId = -1;
    private HashMap<Integer, TaskData> tasks = new HashMap<>();
    private HashMap<Integer, EpicData> epics = new HashMap<>();
    private HashMap<Integer, SubTaskData> subTasks = new HashMap<>();
    private Managers managers = new Managers();
    HistoryManager inMemoryHistoryManager = managers.getHistoryDefault();

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
        addSubTaskToEpics(subTaskData);

        EpicData epicData = epics.get(subTaskData.getEpicId());
        if (epicData != null && TaskData.statuses.NEW.equals(epicData.getStatus())) {
            epicData.setStatus(TaskData.statuses.IN_PROGRESS);
        }
        updateEpic(epicData);
    }

    private void addSubTaskToEpics(SubTaskData subTaskData) {
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
        return tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        EpicData epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subTaskId : epic.getSubTaskIdList()) {
            subTasks.remove(subTaskId);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
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
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (EpicData epicData : epics.values()) {
            epicData.clearSubTaskIdList();
            epicData.setStatus(TaskData.statuses.NEW);
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

    @Override
    public void updateEpicStatus(int id) {
        ArrayList<TaskData.statuses> subTasksStatuses = new ArrayList<>();
        ArrayList<TaskData.statuses> uniqueStatuses = new ArrayList<>();

        for (SubTaskData subTask : subTasks.values()) {
            if (subTask.getEpicId() == id) {
                subTasksStatuses.add(subTask.status);
            }
        }

        for (TaskData.statuses subTasksStatus : subTasksStatuses) {
            if (!uniqueStatuses.contains(subTasksStatus)) {
                uniqueStatuses.add(subTasksStatus);
            }
        }

        if (epics.get(id).getSubTaskIdList().size() == 0 ||
                (uniqueStatuses.size() == 1 && uniqueStatuses.get(0).equals("NEW"))) {
            epics.get(id).setStatus(TaskData.statuses.NEW);
        } else if (uniqueStatuses.size() == 1 && uniqueStatuses.get(0).equals("DONE")) {
            epics.get(id).setStatus(TaskData.statuses.DONE);
        } else {
            epics.get(id).setStatus(TaskData.statuses.IN_PROGRESS);
        }
    }

    private int genID() {
        nextId++;
        return nextId;
    }
}
