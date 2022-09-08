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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File fileForSave = new File("./src/", "example.csv");
    private List<Integer> idHistory = new ArrayList<>();
    private boolean isTasksRead = false;

    public static void main(String[] args) {

        try {
            FileBackedTasksManager fileManager = Managers.getFileBackedTasksManager();

            TaskData newTaskData = new TaskData("Победить в чемпионате по поеданию бургеров", "Нужно тренироваться, едим бургеры!");
            TaskData newTaskData1 = new TaskData("Пробежать марафон", "Попробовать свои силы на марафоне который будет осенью");
            newTaskData1.setStatus(Statuses.IN_PROGRESS);
            EpicData epic0 = new EpicData("Переехать", "Что-то сделать в процессе", Statuses.NEW);
            EpicData epic3 = new EpicData("Переехать3", "Что-то сделать в процессе3", Statuses.NEW);
            SubTaskData subT1 = new SubTaskData("Собрать вещи", "Собирать вещи");
            SubTaskData subT2 = new SubTaskData("Собрать вещи2", "Собирать вещи2");
            SubTaskData subT3 = new SubTaskData("Собрать вещи3", "Собирать вещи3");
            subT3.setStatus(Statuses.IN_PROGRESS);

            fileManager.addToTasks(newTaskData);
            fileManager.addToTasks(newTaskData1);
            fileManager.addToEpics(epic0);
            fileManager.addToEpics(epic3);
            subT1.setEpicId(epic3.getId());
            subT2.setEpicId(epic3.getId());
            subT3.setEpicId(epic3.getId());
            fileManager.addSubTaskToEpics(subT1);
            fileManager.addSubTaskToEpics(subT2);
            fileManager.addSubTaskToEpics(subT3);
            fileManager.addToSubTasks(subT1);
            fileManager.addToSubTasks(subT2);
            fileManager.addToSubTasks(subT3);

            fileManager.getEpicById(epic3.getId());
            fileManager.getEpicById(epic0.getId());
            fileManager.getEpicById(epic3.getId());
            fileManager.getTaskById(newTaskData.getId());
            fileManager.getTaskById(newTaskData1.getId());
            fileManager.getSubTaskById(subT3.getId());
            fileManager.getSubTaskById(subT2.getId());
            fileManager.getSubTaskById(subT1.getId());

        } catch (IOException e) {
            e.getMessage();
        }
    }


    FileBackedTasksManager() {
        loadFromFile(fileForSave);
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(fileForSave)) {
            if (fileForSave.length() == 0) {
                fileWriter.write("id,type,name,status,description,epic" + "\n");
            }

            for (TaskData t : this.getAllTasks()) {
                    fileWriter.write(toString(t) + "\n");
            }
            for (TaskData e : this.getAllEpics()) {
                fileWriter.write(toString(e) + "\n");
            }
            for (TaskData st : this.getAllSubTasks()) {
                fileWriter.write(toString(st) + "\n");
            }

            fileWriter.write("\n");
            fileWriter.write(historyToString(inMemoryHistoryManager) + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка");
        }
    }

    private String toString(TaskData taskData) {
        String result;
        result = taskData.getId() + "," + taskData.getType() + "," + taskData.getName() + "," + taskData.getStatus() + "," + taskData.getDescription();

        if (taskData.getType().equals(DataTypes.SUBTASK)) {
            SubTaskData subTask = (SubTaskData) taskData;
            result = result + ", " + subTask.getEpicId();
        }

        return result;
    }

    private void loadFromFile(File file) {
        try {
            if (!Files.exists(file.toPath())) {
                Files.createFile(file.toPath());
                return;
            }
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String line = br.readLine();


            while (line != null) {
                if(line.length() > 0) {
                    TaskData collectedDataItem = fromString(line);
                    String[] lineValues = line.split(",");
                    String dataType = lineValues[1];
                    if (collectedDataItem != null && collectedDataItem.getId() > nextId) {
                        nextId = collectedDataItem.getId();
                    }

                    switch (dataType) {
                        case "TASK":
                            tasks.put(collectedDataItem.getId(), collectedDataItem);
                            break;
                        case "EPIC":
                            epics.put(collectedDataItem.getId(), (EpicData) collectedDataItem);
                            break;
                        case "SUBTASK":
                            subTasks.put(collectedDataItem.getId(), (SubTaskData) collectedDataItem);
                            break;
                        default:
                            break;
                    }
                } else {
                    isTasksRead = true;
                }

                line = br.readLine();
            }
        } catch(IOException ex) {
            throw new ManagerSaveException("Не удалось прочитать файл");
        }
    }


    private TaskData fromString(String value) {
        String[] lineValues = value.split(",");
        int id = Integer.parseInt(lineValues[0]);
        String dataType = lineValues[1];
        String dataName = lineValues[2];
        String description = lineValues[4];
        Statuses status = Statuses.NEW;
        if(!isTasksRead) {
            status = Statuses.valueOf(lineValues[3]);
        }

        if (dataType.equals("TASK")) {
            return new TaskData(dataName, description, id, status);
        } else if (dataType.equals("EPIC")) {
            EpicData epic;
            epic = new EpicData(dataName, description,id, status);
            return epic;
        } else if (dataType.equals("SUBTASK")) {
            SubTaskData subTask = new SubTaskData(dataName, description, id, status);
            int epicId = 0;
            if (lineValues[5] != null) {
                epicId = Integer.parseInt(lineValues[5].trim());
            }
            subTask.setEpicId(epicId);
            addSubTaskToEpics(subTask);
            return subTask;
        } else {
            return null;
        }
    }

    private static String historyToString(HistoryManager<TaskData> historyManager) {
        StringBuilder result = new StringBuilder();

        for (TaskData item : historyManager.getHistory()) {
            result.append(item.getId()).append(",");
        }
        if (result.length() > 1) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    private static ArrayList<Integer> historyFromString(String value) {
        String[] lineValues = value.split(",");
        ArrayList<Integer> result = new ArrayList<>();

        for(String v : lineValues) {
            result.add(Integer.parseInt(v));
        }
        return result;
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
    public void addToSubTasks(SubTaskData subTask) {
        super.addToSubTasks(subTask);
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

