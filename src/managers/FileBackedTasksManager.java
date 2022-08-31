package managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.DataTypes;
import enums.Statuses;
import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File fileForSave = new File("./src/", "example.csv");
    private HistoryManager<TaskData> inMemoryHistoryManager = Managers.getHistoryDefault();
    private List<Integer> idHistory = new ArrayList<>();

    static void main(String[] args) {

        TaskManager inMemoryTaskManager = Managers.getDefault();

        TaskData newTaskData = new TaskData("Победить в чемпионате по поеданию бургеров", "Нужно тренироваться, едим бургеры!");
        inMemoryTaskManager.addToTasks(newTaskData);
        TaskData newTaskData1 = new TaskData("Пробежать марафон", "Попробовать свои силы на марафоне который будет осенью");
        inMemoryTaskManager.addToTasks(newTaskData1);

        EpicData epic0 = new EpicData("Переехать", "Что-то сделать в процессе", Statuses.NEW);
        inMemoryTaskManager.addToEpics(epic0);
        EpicData epic3 = new EpicData("Переехать3", "Что-то сделать в процессе3", Statuses.NEW);
        inMemoryTaskManager.addToEpics(epic3);

        inMemoryTaskManager.addToTasks(newTaskData);
        inMemoryTaskManager.addToTasks(newTaskData1);

        SubTaskData subT1 = new SubTaskData("Собрать вещи", "Собирать вещи");
        SubTaskData subT2 = new SubTaskData("Собрать вещи2", "Собирать вещи2");
        SubTaskData subT3 = new SubTaskData("Собрать вещи3", "Собирать вещи3");
        subT1.setEpicId(epic3.getId());
        subT2.setEpicId(epic3.getId());
        subT3.setEpicId(epic3.getId());
        inMemoryTaskManager.addToSubTasks(subT1);
        inMemoryTaskManager.addToSubTasks(subT2);
        inMemoryTaskManager.addToSubTasks(subT3);
        epic3.addSubTask(subT1);
        epic3.addSubTask(subT2);
        epic3.addSubTask(subT3);

        inMemoryTaskManager.getTaskById(newTaskData.getId());
        inMemoryTaskManager.getTaskById(newTaskData1.getId());
        inMemoryTaskManager.getEpicById(epic3.getId());
        inMemoryTaskManager.getEpicById(epic0.getId());
        inMemoryTaskManager.getEpicById(epic3.getId());
        inMemoryTaskManager.getTaskById(newTaskData1.getId());

        try {
            FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        } catch (IOException e) {
            e.getMessage();
        }
    }


    private FileBackedTasksManager() throws IOException {
        loadFromFile(fileForSave);
    }

    private void save() throws ManagerSaveException {
        try(Writer fileWriter = new FileWriter(fileForSave, true))  {
            if(!fileForSave.exists()) {
                throw new ManagerSaveException("Такого файла не существует");
            }
            fileWriter.write("id,type,name,status,description,epic");

            for (TaskData t : this.getAllTasks()) {
                fileWriter.write(toString(t));
            }
            for (TaskData e : this.getAllEpics()) {
                fileWriter.write(toString(e));
            }
            for (TaskData st : this.getAllSubTasks()) {
                fileWriter.write(toString(st));
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(inMemoryHistoryManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка");
        }
    }

    private String toString(TaskData taskData) {
        String result;

        result = taskData.getId() + "," + taskData.getType() + "," + taskData.getName() + "," + taskData.getStatus() + "," + taskData.getDescription() + ",";

        if (taskData.getType().equals(DataTypes.SUBTASK)) {
            SubTaskData subTask = (SubTaskData) taskData;
            result = result + subTask.getEpicId();
        }

        return result;
    }

    private void loadFromFile(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String lines = br.readLine();

        String[] linesArr = lines.split("\n");

        boolean tasksIsOver = false;
        for (String line : linesArr) {
            if (!tasksIsOver) {
                if (line.isBlank()) {
                    tasksIsOver = true;
                }
                TaskData collectedDataItem = fromString(line);
                String[] lineValues = line.split(",");

                String dataType = lineValues[1];

                if (dataType.equals("TASK")) {
                    addToTasks(collectedDataItem);
                }
                else if (dataType.equals("EPIC")) {
                    addToEpics((EpicData) collectedDataItem);
                } else {
                    addToSubTasks((SubTaskData) collectedDataItem);
                }
            } else {
                idHistory.addAll(historyFromString(line));
            }
        }

    }


    private TaskData fromString(String value) {
        String[] lineValues = value.split(",");
        String dataType = lineValues[1];
        String dataName = lineValues[2];
        String description = lineValues[4];

        if (dataType.equals("TASK")) {
            return new TaskData(dataName, description);
        }
        else if (dataType.equals("EPIC")) {
            EpicData epic;
            Statuses status = Statuses.valueOf(lineValues[2]);
            epic = new EpicData(dataName, description, status);
            return epic;
        } else {
            SubTaskData subTask = new SubTaskData(dataName, description);
            int epicId = 0;
            if(lineValues[5] != null) {
                epicId = Integer.parseInt(lineValues[5]);
            }
            subTask.setEpicId(epicId);
            addSubTaskToEpics(subTask);
            return subTask;
        }
    }

    private static String historyToString(HistoryManager<TaskData> historyManager) {
        String result = "";

        for (TaskData item : historyManager.getHistory()) {
            result = item.getId() + ",";
        }

        return result;
    }

    private static List<Integer> historyFromString(String value) {
        String[] lineValues = value.split(",");
        List<Integer> result = new ArrayList<>();

        for (String v : lineValues) {
            result.add(Integer.parseInt(v));
        }

        return result;
    }


    @Override
    public void addToSubTasks(SubTaskData subTask) {
        super.addToSubTasks(subTask);
        save();
    }

    @Override
    public int addToEpics(EpicData epicData) {
        super.addToEpics(epicData);
        save();
        return epicData.getId();
    }

    @Override
    public void addToTasks(TaskData taskData) {
        super.addToTasks(taskData);
        save();
    }

    @Override
    protected void addSubTaskToEpics(SubTaskData subTaskData) {
        super.addSubTaskToEpics(subTaskData);
        save();
    }

}

