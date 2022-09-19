package interfaces;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void addToTasks(TaskData taskData);

    int addToEpics(EpicData epicData);

    void addToSubTasks(SubTaskData subTaskData);

    TaskData getTaskById(int id);

    EpicData getEpicById(int id);

    SubTaskData getSubTaskById(int id);

    List<TaskData> getPrioritizedTasks();

    TaskData deleteTaskById(int id);

    EpicData deleteEpicById(int id);

    SubTaskData deleteSubTaskById(int id);

    ArrayList<TaskData> getAllTasks();

    ArrayList<EpicData> getAllEpics();

    ArrayList<SubTaskData> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    void updateTask(TaskData taskData);

    void updateEpic(EpicData epicData);

    void updateSubTask(SubTaskData subTaskData);

    List<Integer> getHistory();
}
