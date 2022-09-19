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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public File fileForSave = new File("./src/", "example.csv");
    private List<Integer> idHistory = new ArrayList<>();
    private boolean isTasksRead = false;

    public static void main(String[] args) {

        try {
            FileBackedTasksManager fileManager = Managers.getFileBackedTasksManager();

            TaskData newTaskData = new TaskData("Победить в чемпионате по поеданию бургеров", "Нужно тренироваться - едим бургеры!");
            TaskData newTaskData1 = new TaskData("Пробежать марафон", "Попробовать свои силы на марафоне который будет осенью");
            newTaskData1.setStatus(Statuses.IN_PROGRESS);
            EpicData epic0 = new EpicData("Переехать", "Что-то сделать в процессе", Statuses.NEW);
            EpicData epic3 = new EpicData("Переехать3", "Что-то сделать в процессе3", Statuses.NEW);
            SubTaskData subT1 = new SubTaskData("Собрать вещи", "Собирать вещи");
            SubTaskData subT2 = new SubTaskData("Собрать вещи2", "Собирать вещи2");
            SubTaskData subT3 = new SubTaskData("Собрать вещи3", "Собирать вещи3");


            newTaskData.setDuration(120);
            newTaskData.setStartDate(2022, 2, 24);
            newTaskData1.setDuration(500);
            newTaskData1.setStartDate(2022, 3, 24);

            fileManager.addToTasks(newTaskData);
            fileManager.addToTasks(newTaskData1);
            fileManager.addToEpics(epic0);

            fileManager.addToEpics(epic3);
            subT1.setEpicId(epic3.getId());
            subT2.setEpicId(epic3.getId());
            subT3.setEpicId(epic3.getId());

            subT1.setDuration(120);
            subT1.setStartDate(2022, 4, 24);
            subT2.setDuration(500);
            subT2.setStartDate(2022, 5, 24);
            subT3.setDuration(500);
            subT3.setStartDate(2022, 5, 25);


            fileManager.addToSubTasks(subT1);
            fileManager.addToSubTasks(subT2);
            fileManager.addToSubTasks(subT3);
            fileManager.addSubTaskToEpics(subT1);
            fileManager.addSubTaskToEpics(subT2);
            fileManager.addSubTaskToEpics(subT3);

            fileManager.getEpicById(epic3.getId());
            fileManager.getEpicById(epic0.getId());
            fileManager.getEpicById(epic3.getId());
            fileManager.getTaskById(newTaskData.getId());
            fileManager.getTaskById(newTaskData1.getId());
            fileManager.getEpicById(epic3.getId());
            fileManager.getSubTaskById(subT2.getId());
            fileManager.getSubTaskById(subT1.getId());
        } catch (IOException e) {
            e.getMessage();
        }
    }


    FileBackedTasksManager() {
        loadFromFile(fileForSave);
    }

    public FileBackedTasksManager(File file) {
        loadFromFile(file);
    }
    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(fileForSave)) {
            fileWriter.write("id,type,name,status,description,startDate,endDate,epic" + "\n");

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
        String taskStart = "";
        String taskEnd = "";
        if (taskData.getStartDate() != null) {
            taskStart = taskData.getStartDate() + ",";
            taskEnd = taskData.getEndTime() + "";
        }
        result = taskData.getId() + "," + taskData.getType() + "," + taskData.getName() + "," + taskData.getStatus() + "," + taskData.getDescription() + "," + taskStart + taskEnd;

        if (taskData.getType().equals(DataTypes.SUBTASK)) {
            SubTaskData subTask = (SubTaskData) taskData;
            result = result + ", " + subTask.getEpicId();
        }

        return result;
    }

    void loadFromFile(File file) {
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
                if (line.length() > 0) {
                    TaskData collectedDataItem = fromString(line);
                    String[] lineValues = line.split(",");
                    String dataType = lineValues[1];
                    if (collectedDataItem != null && collectedDataItem.getId() > nextId) {
                        nextId = collectedDataItem.getId();
                    }

                    if(!isTasksRead) {
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
                        }
                    } else {
                        idHistory.addAll(historyFromString(line));
                    }
                } else {
                    isTasksRead = true;
                }

                line = br.readLine();
            }
        } catch (IOException ex) {
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

        LocalDateTime taskStart = LocalDateTime.now();
        LocalDateTime taskEndTime = LocalDateTime.now();
        if (lineValues.length > 6) {
            taskStart = LocalDateTime.parse(lineValues[5], DateTimeFormatter.ISO_DATE_TIME);
            taskEndTime = LocalDateTime.parse(lineValues[6], DateTimeFormatter.ISO_DATE_TIME);
        }

        switch (dataType) {
            case "TASK":
                TaskData task = new TaskData(dataName, description, id, status);
                task.setStartDate(taskStart);
                task.calcDurationByEndTime(taskEndTime);
                return task;
            case "EPIC":
                EpicData epic;
                epic = new EpicData(dataName, description, id, status);
                epic.setStartDate(taskStart);
                epic.setDuration(Duration.between(taskStart, taskEndTime).toMinutes());
                return epic;
            case "SUBTASK":
                SubTaskData subTask = new SubTaskData(dataName, description, id, status);
                int epicId = 0;
                if (lineValues[7] != null) {
                    epicId = Integer.parseInt(lineValues[7].trim());
                }
                subTask.setEpicId(epicId);
                addSubTaskToEpics(subTask);

                subTask.setStartDate(taskStart);
                subTask.calcDurationByEndTime(taskEndTime);
                return subTask;
            default:
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

        for (String v : lineValues) {
            result.add(Integer.parseInt(v));
        }
        return result;
    }

    public List<Integer> getHistory() {
        return idHistory;
    }

    @Override
    public int addToEpics(EpicData epicData) {
        super.addToEpics(epicData);
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

    public void addSubTaskToEpics(SubTaskData subTaskData) {
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
    public EpicData deleteEpicById(int id) {
        EpicData epic = super.deleteEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTaskData deleteSubTaskById(int id) {
        SubTaskData subTask = super.deleteSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void updateTask(TaskData taskData) {
        super.updateTask(taskData);
        save();
    }

    @Override
    public void updateEpic(EpicData epicData) {
        super.updateEpic(epicData);
        save();
    }

    @Override
    public void updateSubTask(SubTaskData subTaskData) {
        super.updateSubTask(subTaskData);
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

