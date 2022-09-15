package managers;

import dataClasses.TaskData;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager<TaskData> inMemoryHistoryManager;
    TaskManager inMemoryTaskManager;
    TaskData task;

    @BeforeEach
    void beforeEach() {
        inMemoryHistoryManager = Managers.getHistoryDefault();
        inMemoryTaskManager = Managers.getDefault();
        task = new TaskData("task", "desc");
    }

    @Test
    void addEmptyTest() {
        inMemoryTaskManager.addToTasks(task);
        assertEquals(0, inMemoryHistoryManager.getHistory().size());
        inMemoryHistoryManager.add(task);

        assertEquals(task, inMemoryHistoryManager.getHistory().get(0));
    }

    @Test
    void getHistoryEmptyTest() {
        assertEquals(0, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void removeEmptyTest() {
        assertEquals(0, inMemoryHistoryManager.getHistory().size());

        inMemoryHistoryManager.remove(0);

        assertEquals(0, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void removeDataTypeEmptyTest() {
        inMemoryTaskManager.addToTasks(task);
        assertEquals(0, inMemoryHistoryManager.getHistory().size());
        Set<Integer> tasks = new HashSet<>();
        tasks.add(inMemoryTaskManager.getTaskById(task.getId()).getId());

        inMemoryHistoryManager.removeDataType(tasks);

        assertEquals(0, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void addDuplicateEmptyTest() {
        inMemoryTaskManager.addToTasks(task);
        assertEquals(0, inMemoryHistoryManager.getHistory().size());
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);

        assertEquals(List.of(task), inMemoryHistoryManager.getHistory());

    }

    @Test
    void getHistoryDuplicateTest() {
        inMemoryTaskManager.addToTasks(task);
        assertEquals(0, inMemoryHistoryManager.getHistory().size());
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);

        assertEquals(List.of(task), inMemoryHistoryManager.getHistory());
    }

    @Test
    void removeDuplicateTest() {
        inMemoryTaskManager.addToTasks(task);
        assertEquals(0, inMemoryHistoryManager.getHistory().size());
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);

        inMemoryHistoryManager.remove(0);

        assertEquals(0, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void removeDataTypeDuplicateTest() {
        inMemoryTaskManager.addToTasks(task);
        assertEquals(0, inMemoryHistoryManager.getHistory().size());
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);
        assertEquals(1, inMemoryHistoryManager.getHistory().size());
        Set<Integer> tasks = new HashSet<>();
        tasks.add(inMemoryTaskManager.getTaskById(task.getId()).getId());

        inMemoryHistoryManager.removeDataType(tasks);

        assertEquals(0, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void deleteFromStartOfHistory() {
        TaskData task1 = new TaskData("task1", "desc");
        TaskData task2 = new TaskData("task2", "desc");
        inMemoryTaskManager.addToTasks(task);
        inMemoryTaskManager.addToTasks(task1);
        inMemoryTaskManager.addToTasks(task2);
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task1.getId());

        inMemoryHistoryManager.remove(2);

        assertEquals(List.of(task, task1), inMemoryHistoryManager.getHistory());
    }

    @Test
    void deleteFromMiddleOfHistory() {
        TaskData task1 = new TaskData("task1", "desc");
        TaskData task2 = new TaskData("task2", "desc");
        inMemoryTaskManager.addToTasks(task1);
        inMemoryTaskManager.addToTasks(task);
        inMemoryTaskManager.addToTasks(task2);
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getTaskById(task.getId());

        inMemoryHistoryManager.remove(1);

        assertEquals(List.of(task2, task), inMemoryHistoryManager.getHistory());

    }

    @Test
    void deleteFromEndOfHistory() {
        TaskData task1 = new TaskData("task1", "desc");
        TaskData task2 = new TaskData("task2", "desc");
        inMemoryTaskManager.addToTasks(task1);
        inMemoryTaskManager.addToTasks(task);
        inMemoryTaskManager.addToTasks(task2);
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getTaskById(task.getId());

        inMemoryHistoryManager.remove(0);

        assertEquals(List.of(task2, task1), inMemoryHistoryManager.getHistory());
    }
}