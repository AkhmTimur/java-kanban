import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    int nextId = 0;
    HashMap<Integer, TaskData> tasks = new HashMap<>();
    HashMap<Integer, EpicData> epics = new HashMap<>();
    HashMap<Integer, SubTaskData> subTasks = new HashMap<>();

    void addToTasks(TaskData taskData);

    int addToEpics(EpicData epicData);

    void addToSubTasks(SubTaskData subTaskData);

    private void addSubTaskToEpics(SubTaskData subTaskData) {
        epics.get(subTaskData.getEpicId()).addSubTask(subTaskData);
    }

    TaskData getTaskById(int id);

    EpicData getEpicById(int id);

    SubTaskData getSubTaskById(int id);

    TaskData deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    ArrayList<TaskData> getAllTasks();

    ArrayList<EpicData> getAllEpics();

    ArrayList<SubTaskData> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    void updateTask(TaskData taskData);

    void updateEpic(EpicData epicData);

    void updateSubTask(SubTaskData subTaskData);

    void updateEpicStatus(int id);
}
