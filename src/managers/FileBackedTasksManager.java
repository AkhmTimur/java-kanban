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
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File fileForSave = new File("./src/", "example.csv");
    private HistoryManager<TaskData> inMemoryHistoryManager = Managers.getHistoryDefault();
    private List<Integer> idHistory = new ArrayList<>();
    private List<String> printedTasks = new ArrayList<>();

    public static void main(String[] args) {

        try {
            FileBackedTasksManager fileManager = Managers.getFileBackedTasksManager();

            TaskManager inMemoryTaskManager = Managers.getDefault();

            TaskData newTaskData = new TaskData("Победить в чемпионате по поеданию бургеров", "Нужно тренироваться, едим бургеры!");
            TaskData newTaskData1 = new TaskData("Пробежать марафон", "Попробовать свои силы на марафоне который будет осенью");


            EpicData epic0 = new EpicData("Переехать", "Что-то сделать в процессе", Statuses.NEW);
            EpicData epic3 = new EpicData("Переехать3", "Что-то сделать в процессе3", Statuses.NEW);

            SubTaskData subT1 = new SubTaskData("Собрать вещи", "Собирать вещи");
            SubTaskData subT2 = new SubTaskData("Собрать вещи2", "Собирать вещи2");
            SubTaskData subT3 = new SubTaskData("Собрать вещи3", "Собирать вещи3");

            fileManager.addToTasks(newTaskData);
            fileManager.addToTasks(newTaskData1);

            fileManager.addToEpics(epic0);
            fileManager.addToEpics(epic3);


            subT1.setEpicId(epic3.getId());
            subT2.setEpicId(epic3.getId());
            subT3.setEpicId(epic3.getId());
            fileManager.addToSubTasks(subT1);
            fileManager.addToSubTasks(subT2);
            fileManager.addToSubTasks(subT3);
            epic3.addSubTask(subT1);
            epic3.addSubTask(subT2);
            epic3.addSubTask(subT3);


        } catch (IOException e) {
            e.getMessage();
        }
    }


    FileBackedTasksManager() throws IOException {
        loadFromFile(fileForSave);
        System.out.println("1");
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(fileForSave, true)) {
            if (!fileForSave.exists()) {
                File newFile = new File("./src/", "example1.csv");
                return;
            }
            if(fileForSave.length() == 0) {
                fileWriter.write("id,type,name,status,description,epic" + "\n");
            }


            for (TaskData t : this.getAllTasks()) {
                if(!printedTasks.contains(toString(t).substring(0, 1))) {
                    fileWriter.write(toString(t) + "\n");
                    printedTasks.add(toString(t).substring(0, 1));
                }
            }
            for (TaskData e : this.getAllEpics()) {
                if(!printedTasks.contains(toString(e).substring(0, 1))) {
                    fileWriter.write(toString(e) + "\n");
                    printedTasks.add(toString(e).substring(0, 1));
                }
            }
            for (TaskData st : this.getAllSubTasks()) {
                if(!printedTasks.contains(toString(st).substring(0, 1))) {
                    fileWriter.write(toString(st) + "\n");
                    printedTasks.add(toString(st).substring(0, 1));
                }
            }
            fileWriter.write(historyToString(inMemoryHistoryManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка");
        }
    }

    private String toString(TaskData taskData) {
        String result;

        result = taskData.getId() + "," + taskData.getType() + "," + taskData.getName() + "," + taskData.getStatus() + "," + taskData.getDescription();

        if (taskData.getType().equals(DataTypes.SUBTASK)) {
            SubTaskData subTask = (SubTaskData) taskData;
            result = result + ", " +  subTask.getEpicId();
        }

        return result;
    }

    private void loadFromFile(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String line = br.readLine();

        while(line != null) {
            TaskData collectedDataItem = fromString(line);
            String[] lineValues = line.split(",");

            String dataType = lineValues[1];

            if (dataType.equals("TASK")) {
                tasks.put(collectedDataItem.getId(), collectedDataItem);
            } else if (dataType.equals("EPIC")) {
                epics.put(collectedDataItem.getId(), (EpicData) collectedDataItem);
            } else {
                subTasks.put(collectedDataItem.getId(), (SubTaskData) collectedDataItem);
            }

            idHistory.add(historyFromString(line));

            line = br.readLine();
        }
    }


    private TaskData fromString(String value) {
        String[] lineValues = value.split(",");
        String dataType = lineValues[1];
        String dataName = lineValues[2];
        String description = lineValues[4];

        if (dataType.equals("TASK")) {
            return new TaskData(dataName, description, genID());
        } else if (dataType.equals("EPIC")) {
            EpicData epic;
            Statuses status = Statuses.valueOf(lineValues[3]);
            epic = new EpicData(dataName, description, status, genID());
            return epic;
        } else {
            SubTaskData subTask = new SubTaskData(dataName, description, genID());
            int epicId = 0;
            if (lineValues[5] != null) {
                epicId = Integer.parseInt(lineValues[5].trim());
            }
            subTask.setEpicId(epicId);
            addSubTaskToEpics(subTask);
            return subTask;
        }
    }

    private static String historyToString(HistoryManager<TaskData> historyManager) {
        StringBuilder result = new StringBuilder();

        for (TaskData item : historyManager.getHistory()) {
            result.append(item.getId()).append(",");
        }

        if(result.length() > 1) {
            result.substring(0, result.length() - 1);
        }

        return result.toString();
    }

    private static Integer historyFromString(String value) {
        String[] lineValues = value.split(",");

        return Integer.parseInt(lineValues[0]);
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
        epics.get(subTaskData.getEpicId()).addSubTask(subTaskData);
    }

    @Override
    public TaskData getTaskById(int id) {
        TaskData task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public EpicData getEpicById(int id) {
        EpicData epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTaskData getSubTaskById(int id) {
        SubTaskData subtask = super.getSubTaskById(id);
        save();
        return subtask;
    }

    @Override
    public TaskData deleteTaskById(int id) {
        TaskData task = super.deleteTaskById(id);
        save();
        return task;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void updateTask(TaskData taskData) {
        super.updateTask(taskData);
        save();
    }

    @Override
    public void updateEpic(EpicData epicData) {
        super.updateTask(epicData);
        save();
    }

    @Override
    public void updateSubTask(SubTaskData subTaskData) {
        super.updateTask(subTaskData);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }


}

