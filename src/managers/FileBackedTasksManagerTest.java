package managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
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
        assertEquals(0, fileManager.getAllTasks().size());

        fileManager.addToTasks(task);

        assertEquals(task, fileManager.getTaskById(0));
    }

    @Test
    void emptyTasksLoadTest() {
        /*Не знаю как решить вопрос изначальным заполнением файл менеджера при инициации его объекта.
         Менеджер изначально заполняется всем типами данных.
         Поэтому руками очищал файл и комментил добавление в классе FileBackedTasksManager*/
        /*В голову пришла только идея проверки, когда я комменчу код в самом менеджере. Аналогично для остальных load
        методов*/

        assertEquals(0, fileManager.getAllTasks().size());
    }

    @Test
    void epicWithOutSubtasksSaveTest() {
        assertEquals(0, fileManager.getAllEpics().size());

        fileManager.addToEpics(epic0);

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
        assertEquals(0, historyManager.getHistory().size());

        historyManager.add(fileManager.getEpicById(2));

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void emptyHistoryLoadTest() {
        assertEquals(0, fileManager.getHistory().size());

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

        fileManager.addSubTaskToEpics(subT1);
        fileManager.addSubTaskToEpics(subT2);
        fileManager.addToSubTasks(subT1);
        fileManager.addToSubTasks(subT2);

        assertEquals(LocalDateTime.of(2022, 3, 24, 4, 0), epic3.getEndTime());
    }
}
