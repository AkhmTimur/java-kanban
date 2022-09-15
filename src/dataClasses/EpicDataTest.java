package dataClasses;

import enums.Statuses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicDataTest {

    static EpicData epic;
    static SubTaskData subtask1;

    @BeforeEach
    public static void createSomething() {
        epic = new EpicData("epicName", "description", 0, Statuses.NEW);
        subtask1 = new SubTaskData("epicName", "description", 1, Statuses.NEW);
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
}