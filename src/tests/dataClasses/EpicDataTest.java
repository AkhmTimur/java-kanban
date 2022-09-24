package tests.dataClasses;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import enums.Statuses;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicDataTest {

    static EpicData epic;
    static SubTaskData subtask1;
    static SubTaskData subT1;
    static SubTaskData subT2;
    static SubTaskData subT3;
    private LocalDateTime endTime;
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void createSomething() {
        inMemoryTaskManager = new InMemoryTaskManager();
        epic = new EpicData("epicName", "description", 0, Statuses.NEW);
        subtask1 = new SubTaskData("epicName", "description", 1, Statuses.NEW);
        subT1 = new SubTaskData("Собрать вещи", "Собирать вещи");
        subT2 = new SubTaskData("Собрать вещи2", "Собирать вещи2");
        subT3 = new SubTaskData("Собрать вещи3", "Собирать вещи3");
        epic.addSubTask(subtask1);
    }

    @Test
    public void getSubTaskIdListTest() {
        assertEquals(List.of(1), epic.getSubTaskIdList());
    }

    @Test
    public void addSubTaskTest() {
        SubTaskData subtask2 = new SubTaskData("epicName", "description", 2, Statuses.NEW);
        epic.addSubTask(subtask2);

        ArrayList<Integer> subtaskId = epic.getSubTaskIdList();

        assertEquals(List.of(1, 2), subtaskId);
    }

    @Test
    public void removeSubTaskTest() {
        SubTaskData subtask2 = new SubTaskData("epicName", "description", 2, Statuses.NEW);
        epic.addSubTask(subtask2);

        epic.removeSubTask(2);

        assertEquals(List.of(1), epic.getSubTaskIdList());
    }

    @Test
    public void clearSubTaskIdListTest() {
        epic.clearSubTaskIdList();

        assertEquals(0, epic.getSubTaskIdList().size());
    }

    @Test
    void epicDurationWhenAddSubTasksTest() {
        inMemoryTaskManager.addToEpics(epic);
        inMemoryTaskManager.deleteAllSubTasks();
        SubTaskData subT1 = new SubTaskData("Собрать вещи", "Собирать вещи");
        SubTaskData subT2 = new SubTaskData("Собрать вещи2", "Собирать вещи2");
        SubTaskData subT3 = new SubTaskData("Собрать вещи3", "Собирать вещи3");

        subT1.setEpicId(epic.getId());
        subT2.setEpicId(epic.getId());
        subT3.setEpicId(epic.getId());

        subT1.setDuration(120);
        subT1.setStartDate(2022, 4, 24);
        subT2.setDuration(500);
        subT2.setStartDate(2022, 5, 24);
        subT3.setDuration(500);
        subT3.setStartDate(2022, 5, 25);

        inMemoryTaskManager.addToSubTasks(subT1);
        inMemoryTaskManager.addToSubTasks(subT2);
        inMemoryTaskManager.addToSubTasks(subT3);
        assertEquals(1120, epic.getDuration().toMinutes());
    }

    @Test
    void epicDurationWhenDeleteSubtaskTest() {
        inMemoryTaskManager.addToEpics(epic);
        inMemoryTaskManager.deleteAllSubTasks();

        subT1.setEpicId(epic.getId());
        subT2.setEpicId(epic.getId());
        subT3.setEpicId(epic.getId());

        subT1.setDuration(120);
        subT1.setStartDate(2022, 4, 24);
        subT2.setDuration(500);
        subT2.setStartDate(2022, 5, 24);
        subT3.setDuration(500);
        subT3.setStartDate(2022, 5, 25);

        inMemoryTaskManager.addToSubTasks(subT1);
        inMemoryTaskManager.addToSubTasks(subT2);
        inMemoryTaskManager.addToSubTasks(subT3);
        assertEquals(1120, epic.getDuration().toMinutes());

        inMemoryTaskManager.deleteSubTaskById(subT3.getId());
        assertEquals(620, epic.getDuration().toMinutes());
    }

    @Test
    void epicEndTimeWhenAddSubTasksTest() {
        inMemoryTaskManager.addToEpics(epic);
        subT1.setEpicId(epic.getId());
        subT2.setEpicId(epic.getId());

        assertNull(epic.getStartDate());

        subT1.setDuration(120);
        subT1.setStartDate(2022, 2, 24);

        subT2.setDuration(240);
        subT2.setStartDate(2022, 3, 24);

        inMemoryTaskManager.addToSubTasks(subT1);
        inMemoryTaskManager.addToSubTasks(subT2);

        assertEquals(LocalDateTime.of(2022, 3, 24, 4, 0), epic.getEndTime());
    }

    @Test
    void epicEndTimeWhenDeleteSubTasksTest() {
        inMemoryTaskManager.addToEpics(epic);
        subT1.setEpicId(epic.getId());
        subT2.setEpicId(epic.getId());
        subT3.setEpicId(epic.getId());
        assertNull(epic.getEndTime());

        subT1.setDuration(120);
        subT1.setStartDate(2022, 2, 24);
        subT2.setDuration(240);
        subT2.setStartDate(2022, 3, 24);
        subT3.setDuration(500);
        subT3.setStartDate(2022, 5, 25);
        inMemoryTaskManager.addToSubTasks(subT1);
        inMemoryTaskManager.addToSubTasks(subT2);
        inMemoryTaskManager.addToSubTasks(subT3);
        assertEquals(LocalDateTime.of(2022, 5, 25, 8, 20), epic.getEndTime());

        inMemoryTaskManager.deleteSubTaskById(subT3.getId());
        assertEquals(LocalDateTime.of(2022, 3, 24, 4, 0), epic.getEndTime());
    }
}