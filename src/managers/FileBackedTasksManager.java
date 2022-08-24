package managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
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
    private static File fileForSave = new File("./src/", "example.csv");
    private static HistoryManager<TaskData> inMemoryHistoryManager = Managers.getHistoryDefault();
    private static TaskManager inMemoryTaskManager = Managers.getDefault();
    private static List<Integer> idHistory = new ArrayList<>();

    static void main(String[] args) {
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

    private void save() {
        try {
            Writer fileWriter = new FileWriter(fileForSave, true);
            fileWriter.write("id,type,name,status,description,epic");

            for(TaskData t: this.getAllTasks()) {
                fileWriter.write(toString(t));
            }
            for(TaskData e: this.getAllEpics()) {
                fileWriter.write(toString(e));
            }
            for(TaskData st: this.getAllSubTasks()) {
                fileWriter.write(toString(st));
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(inMemoryHistoryManager));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private String toString(TaskData taskData) {
        String result;

        result = taskData.getId() + "," +  taskData.print() + "," + taskData.getName() + "," + taskData.status + "," + taskData.getDescription() + ",";

        if(taskData.print().equals("SUBTASK")) {
            SubTaskData subTask = (SubTaskData) taskData;
            result = result + subTask.getEpicId();
        }

        return result;
    }

    private static void loadFromFile(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String lines = br.readLine();

        String[] linesArr = lines.split("\n");

        boolean tasksIsOver = false;
        for(String line: linesArr) {
            if(!tasksIsOver) {
                if(line.isBlank()) {
                    tasksIsOver = true;
                }
                String[] lineValues = line.split(",");
                if(lineValues[1].equals("TASK")) inMemoryTaskManager.addToTasks(fromString(line));
                else if(lineValues[1].equals("EPIC")) {
                    inMemoryTaskManager.addToEpics((EpicData) fromString(line));
                } else inMemoryTaskManager.addToSubTasks((SubTaskData) fromString(line));
            } else {
                idHistory.addAll(historyFromString(line));
            }
        }

    }


    private static TaskData fromString(String value) {
        String[] lineValues = value.split(",");

        if(lineValues[1].equals("TASK")) return new TaskData(lineValues[2], lineValues[4]);
        else if(lineValues[1].equals("EPIC")) {
            EpicData epic;
            if(lineValues[3].equals("NEW")) {
                epic = new EpicData(lineValues[2], lineValues[4], Statuses.NEW);
            } else if(lineValues[3].equals("IN_PROGRESS")) {
                epic = new EpicData(lineValues[2], lineValues[4], Statuses.IN_PROGRESS);
            } else {
                epic = new EpicData(lineValues[2], lineValues[4], Statuses.DONE);
            }
            return epic;
        } else return new SubTaskData(lineValues[2], lineValues[4]);
    }

    private static String historyToString(HistoryManager historyManager) {
        String result = "";

        for(Object item : historyManager.getHistory()) {
            TaskData itemM = (TaskData) item;
            result = itemM.getId() + ",";
        }

        return result;
    }

    private static List<Integer> historyFromString(String value) {
        String[] lineValues = value.split(",");
        List<Integer> result = new ArrayList<>();

        for(String v: lineValues) {
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
}
