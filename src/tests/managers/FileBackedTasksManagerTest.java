package tests.managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import managers.FileBackedTasksManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {

    FileBackedTasksManager fileManager;
    HistoryManager<TaskData> historyManager;
    TaskData newTaskData;
    TaskData newTaskData1;
    EpicData epic3;
    SubTaskData subT3;

    File fileForSave;
    TaskData task;
    EpicData epic0;
    SubTaskData subT1;
    SubTaskData subT2;

    @BeforeEach
    void beforeEach() {
        try {
            fileManager = Managers.getFileBackedTasksManager();
        } catch (IOException ex) {
            throw new ManagerSaveException("Не удалось прочитать файл");

        }
        newTaskData = new TaskData("Победить в чемпионате по поеданию бургеров", "Нужно тренироваться, едим бургеры!");
        newTaskData1 = new TaskData("Пробежать марафон", "Попробовать свои силы на марафоне который будет осенью");
        epic3 = new EpicData("Переехать3", "Что-то сделать в процессе3", Statuses.NEW);
        subT3 = new SubTaskData("Собрать вещи3", "Собирать вещи3");
        historyManager = Managers.getHistoryDefault();
        task = new TaskData("task", "desc");
        epic0 = new EpicData("Переехать", "Что-то сделать в процессе", Statuses.NEW);
        subT1 = new SubTaskData("Собрать вещи", "Собирать вещи");
        subT2 = new SubTaskData("Собрать вещи2", "Собирать вещи2");
    }

    @Test
    void emptyTasksSaveTest() {
        File file = new File("./src/", "test.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        assertEquals(0, fileBackedTasksManager.getAllTasks().size());

        fileBackedTasksManager.addToTasks(task);

        assertEquals(task, fileBackedTasksManager.getTaskById(task.getId()));
    }

    @Test
    void emptyTasksLoadTest() {
        File file = new File("./src/", "test.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        assertEquals(0, fileBackedTasksManager.getAllTasks().size());
    }

    @Test
    void epicWithOutSubtasksSaveTest() {
        fileManager.deleteAllEpics();
        fileManager.addToEpics(epic0);
        assertEquals(0, fileManager.getEpicById(epic0.getId()).getSubTaskIdList().size());

        assertEquals(0, epic0.getSubTaskIdList().size());
        assertEquals(1, fileManager.getAllEpics().size());
    }

    @Test
    void epicWithOutSubtasksLoadTest() {
        fileManager.getEpicById(2);

        assertEquals(0, fileManager.getEpicById(2).getSubTaskIdList().size());
    }

    @Test
    void emptyHistorySaveTest() {
        File file = new File("./src/", "test.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        assertEquals(0, historyManager.getHistory().size());
        fileBackedTasksManager.addToEpics(epic0);

        historyManager.add(fileBackedTasksManager.getEpicById(epic0.getId()));

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void emptyHistoryLoadTest() {
        assertEquals(0, historyManager.getHistory().size());

        File file = new File("./src/", "example.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        newTaskData1.setDuration(240);
        newTaskData1.setStartDate(2022, 3, 24);
        fileBackedTasksManager.addToTasks(newTaskData);
        fileBackedTasksManager.addToTasks(newTaskData1);
        subT1.setDuration(120);
        subT1.setStartDate(2022, 2, 25);
        subT2.setDuration(240);
        subT2.setStartDate(2022, 2, 26);
        fileBackedTasksManager.addToEpics(epic3);
        subT1.setEpicId(epic3.getId());
        subT2.setEpicId(epic3.getId());
        fileBackedTasksManager.getTaskById(0);
        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.getEpicById(2);

        assertEquals(3, fileBackedTasksManager.getHistory().size());
    }

    @Test
    void epicStartDateAndEndDateTimeTest() {
        fileManager.addToEpics(epic3);
        subT1.setEpicId(epic3.getId());
        subT2.setEpicId(epic3.getId());

        assertNull(epic3.getStartDate());

        subT1.setDuration(120);
        subT1.setStartDate(2022, 2, 24);

        subT2.setDuration(240);
        subT2.setStartDate(2022, 3, 24);

        fileManager.addToSubTasks(subT1);
        fileManager.addToSubTasks(subT2);

        assertEquals(LocalDateTime.of(2022, 3, 24, 4, 0), epic3.getEndTime());
    }
}
