package tests.managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    T inMemoryTaskManager;
    TaskData task;
    TaskData task1;
    EpicData epic;
    EpicData epic1;
    SubTaskData subTask;
    SubTaskData subTask1;


    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = getTaskManager();
        task = new TaskData("taskName", "desc");
        task1 = new TaskData("taskName1", "desc");
        epic = new EpicData("epicName", "desc", Statuses.NEW);
        epic1 = new EpicData("epicName1", "desc", Statuses.NEW);
        subTask = new SubTaskData("subTaskName", "desc");
        subTask1 = new SubTaskData("subTaskName1", "desc");

        task.setDuration(120);
        task.setStartDate(2022, 2, 24);
        task1.setDuration(240);
        task1.setStartDate(2022, 3, 24);
        subTask.setDuration(120);
        subTask.setStartDate(2022, 2, 25);
        subTask1.setDuration(240);
        subTask1.setStartDate(2022, 2, 26);
    }

    abstract protected T getTaskManager();

    @Test
    void addToTasksTest() {
        inMemoryTaskManager.addToTasks(task);

        assertEquals(task, inMemoryTaskManager.getAllTasks().get(0));
    }

    @Test
    void addToEpicsTest() {
        inMemoryTaskManager.addToEpics(epic);

        assertEquals(epic, inMemoryTaskManager.getAllEpics().get(0));
    }

    @Test
    void addToSubTasksTest() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);

        assertEquals(subTask, inMemoryTaskManager.getAllSubTasks().get(0));
    }

    @Test
    void getTaskByIdTest() {
        inMemoryTaskManager.addToTasks(task);

        assertEquals(task, inMemoryTaskManager.getTaskById(0));
    }

    @Test
    void getEpicByIdTest() {
        inMemoryTaskManager.addToEpics(epic);

        assertEquals(epic, inMemoryTaskManager.getEpicById(0));
    }

    @Test
    void getSubTaskByIdTest() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);

        assertEquals(subTask, inMemoryTaskManager.getSubTaskById(1));
    }

    @Test
    void deleteTaskByIdTest() {
        inMemoryTaskManager.deleteAllTasks();
        inMemoryTaskManager.addToTasks(task);
        inMemoryTaskManager.deleteTaskById(task.getId());

        assertNull(inMemoryTaskManager.getTaskById(0));
    }

    @Test
    void deleteEpicByIdTest() {
        inMemoryTaskManager.addToEpics(epic);
        inMemoryTaskManager.deleteEpicById(0);

        assertNull(inMemoryTaskManager.getEpicById(0));
    }

    @Test
    void deleteSubTaskByIdTest() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);
        inMemoryTaskManager.deleteSubTaskById(subTask.getId());

        assertNull(inMemoryTaskManager.getSubTaskById(1));
        assertEquals(0, inMemoryTaskManager.getAllSubTasks().size());
    }

    @Test
    void getAllTasksTest() {
        inMemoryTaskManager.addToTasks(task);
        inMemoryTaskManager.addToTasks(task1);

        assertEquals(List.of(task, task1), inMemoryTaskManager.getAllTasks());
    }

    @Test
    void getAllEpicsTest() {
        inMemoryTaskManager.addToEpics(epic);
        inMemoryTaskManager.addToEpics(epic1);

        assertEquals(List.of(epic, epic1), inMemoryTaskManager.getAllEpics());
    }

    @Test
    void getAllSubTasksTest() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        subTask1.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);
        inMemoryTaskManager.addToSubTasks(subTask1);

        assertEquals(List.of(subTask, subTask1), inMemoryTaskManager.getAllSubTasks());
    }

    @Test
    void updateTaskTest() {
        inMemoryTaskManager.addToTasks(task);
        task.setStatus(Statuses.DONE);

        inMemoryTaskManager.updateTask(task);
        assertEquals(Statuses.DONE, inMemoryTaskManager.getTaskById(0).getStatus());
    }

    @Test
    void updateEpicTest() {
        inMemoryTaskManager.addToEpics(epic);
        epic.setStatus(Statuses.DONE);

        inMemoryTaskManager.updateEpic(epic);
        assertEquals(Statuses.DONE, inMemoryTaskManager.getEpicById(0).getStatus());
    }

    @Test
    void updateSubTaskTest() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);
        subTask.setStatus(Statuses.DONE);

        inMemoryTaskManager.updateSubTask(subTask);
        assertEquals(Statuses.DONE, inMemoryTaskManager.getSubTaskById(1).getStatus());
    }

    @Test
    void addToTasksTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllTasks().size());
        inMemoryTaskManager.addToTasks(task);

        assertEquals(1, inMemoryTaskManager.getAllTasks().size());
    }

    @Test
    void addToEpicsTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllEpics().size());
        inMemoryTaskManager.addToEpics(epic);

        assertEquals(1, inMemoryTaskManager.getAllEpics().size());
    }

    @Test
    void addToSubTasksTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllSubTasks().size());
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(0);
        inMemoryTaskManager.addToSubTasks(subTask);

        assertEquals(1, inMemoryTaskManager.getAllSubTasks().size());
    }

    @Test
    void getTaskByIdTestEmpty() {
        assertNull(inMemoryTaskManager.getTaskById(0));
    }

    @Test
    void getEpicByIdTestEmpty() {
        assertNull(inMemoryTaskManager.getEpicById(0));
    }

    @Test
    void getSubTaskByIdTestEmpty() {
        assertNull(inMemoryTaskManager.getSubTaskById(0));
    }

    @Test
    void deleteTaskByIdTestEmpty() {
        inMemoryTaskManager.deleteTaskById(0);
        assertNull(inMemoryTaskManager.getTaskById(0));
    }

    @Test
    void deleteEpicByIdTestEmpty() {
        inMemoryTaskManager.deleteEpicById(0);
        assertNull(inMemoryTaskManager.getEpicById(0));
    }

    @Test
    void deleteSubTaskByIdTestEmpty() {
        assertNull(inMemoryTaskManager.getSubTaskById(0));
    }

    @Test
    void getAllTasksTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllTasks().size());
    }

    @Test
    void getAllEpicsTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllEpics().size());
    }

    @Test
    void getAllSubTasksTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllSubTasks().size());
    }

    @Test
    void deleteAllTasksTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllTasks().size());
        inMemoryTaskManager.addToTasks(task);
        inMemoryTaskManager.addToTasks(task1);
        assertEquals(2, inMemoryTaskManager.getAllTasks().size());

        inMemoryTaskManager.deleteAllTasks();

        assertEquals(0, inMemoryTaskManager.getAllTasks().size());
    }

    @Test
    void deleteAllEpicsTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllEpics().size());
        inMemoryTaskManager.addToEpics(epic);
        inMemoryTaskManager.addToEpics(epic1);
        assertEquals(2, inMemoryTaskManager.getAllEpics().size());

        inMemoryTaskManager.deleteAllEpics();

        assertEquals(0, inMemoryTaskManager.getAllEpics().size());
    }

    @Test
    void deleteAllSubTasksTestEmpty() {
        assertEquals(0, inMemoryTaskManager.getAllSubTasks().size());
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        subTask1.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);
        inMemoryTaskManager.addToSubTasks(subTask1);
        assertEquals(2, inMemoryTaskManager.getAllSubTasks().size());

        inMemoryTaskManager.deleteAllSubTasks();

        assertEquals(0, inMemoryTaskManager.getAllSubTasks().size());
    }

    @Test
    void getTaskByIdTestWrong() {
        inMemoryTaskManager.addToTasks(task);

        assertNull(inMemoryTaskManager.getTaskById(15));
    }

    @Test
    void getEpicByIdTestWrong() {
        inMemoryTaskManager.addToEpics(epic);

        assertNull(inMemoryTaskManager.getEpicById(15));
    }

    @Test
    void getSubTaskByIdTestWrong() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);

        assertNull(inMemoryTaskManager.getSubTaskById(15));
    }

    @Test
    void deleteTaskByIdTestWrong() {
        inMemoryTaskManager.addToTasks(task);

        inMemoryTaskManager.deleteTaskById(15);

        assertNull(inMemoryTaskManager.getTaskById(15));
    }

    @Test
    void deleteEpicByIdTestWrong() {
        inMemoryTaskManager.addToEpics(epic);

        inMemoryTaskManager.deleteEpicById(15);

        assertNull(inMemoryTaskManager.getEpicById(15));
    }

    @Test
    void deleteSubTaskByIdTestWrong() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);

        inMemoryTaskManager.deleteSubTaskById(15);

        assertNull(inMemoryTaskManager.getSubTaskById(15));
    }

    @Test
    void updateTaskTestWrong() {
        inMemoryTaskManager.addToTasks(task);

        inMemoryTaskManager.updateTask(task);

        assertNull(inMemoryTaskManager.getTaskById(15));
    }

    @Test
    void updateEpicTestWrong() {
        inMemoryTaskManager.addToEpics(epic);

        inMemoryTaskManager.updateEpic(epic);

        assertNull(inMemoryTaskManager.getEpicById(15));
    }

    @Test
    void updateSubTaskTestWrong() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);

        inMemoryTaskManager.updateSubTask(subTask);

        assertNull(inMemoryTaskManager.getSubTaskById(15));
    }

    @Test
    void isSubtaskContainsEpicId() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);

        assertEquals(epic.getId(), inMemoryTaskManager.getSubTaskById(subTask.getId()).getEpicId());
    }

    @Test
    void calcEpicStatusTest() {
        inMemoryTaskManager.addToEpics(epic);
        subTask.setEpicId(epic.getId());
        subTask1.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subTask);
        inMemoryTaskManager.addToSubTasks(subTask1);

        assertEquals(Statuses.NEW, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());

        subTask.setStatus(Statuses.IN_PROGRESS);
        inMemoryTaskManager.updateSubTask(subTask);

        assertEquals(Statuses.IN_PROGRESS, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());

        subTask.setStatus(Statuses.DONE);;
        inMemoryTaskManager.updateSubTask(subTask);

        assertEquals(Statuses.IN_PROGRESS, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());

        subTask1.setStatus(Statuses.DONE);
        inMemoryTaskManager.updateSubTask(subTask);

        assertEquals(Statuses.DONE, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());
    }
}