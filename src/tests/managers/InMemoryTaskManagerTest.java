package tests.managers;

import dataClasses.EpicData;
import dataClasses.SubTaskData;
import enums.Statuses;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    static EpicData epic;
    static InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void createEpic() {
        inMemoryTaskManager = new InMemoryTaskManager();
        epic = new EpicData("epicName", "desc", 0, Statuses.NEW);
    }

    @Test
    void epicStatusWithEmptySubtasks() {
        inMemoryTaskManager.addToEpics(epic);
        inMemoryTaskManager.updateEpicStatus(epic.getId());

        assertEquals(Statuses.NEW, epic.getStatus());
    }

    @Test
    void epicStatusWithNewSubtasks() {
        SubTaskData subtask = new SubTaskData("subtaskName", "desc");
        inMemoryTaskManager.addToEpics(epic);
        subtask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subtask);

        assertEquals(Statuses.NEW, epic.getStatus());
    }

    @Test
    void epicStatusWithDoneSubtasks() {
        inMemoryTaskManager.addToEpics(epic);
        SubTaskData subtask = new SubTaskData("subtaskName", "desc");
        subtask.setEpicId(epic.getId());
        subtask.setStatus(Statuses.DONE);
        inMemoryTaskManager.addToSubTasks(subtask);

        assertEquals(Statuses.DONE, epic.getStatus());
    }

    @Test
    void epicStatusWithNewAndDoneStatus() {
        inMemoryTaskManager.addToEpics(epic);
        SubTaskData subtask = new SubTaskData("subtaskName", "desc");
        subtask.setEpicId(epic.getId());
        inMemoryTaskManager.addToSubTasks(subtask);
        SubTaskData subtask1 = new SubTaskData("subtaskName1", "desc");
        subtask1.setEpicId(epic.getId());
        subtask1.setStatus(Statuses.DONE);
        inMemoryTaskManager.addToSubTasks(subtask1);

        assertEquals(Statuses.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void epicStatusWithInProgressSubtasks() {
        inMemoryTaskManager.addToEpics(epic);
        SubTaskData subtask = new SubTaskData("subtaskName", "desc");
        subTask.setEpicId(epic.getId());
        subTask.setStatus(Statuses.IN_PROGRESS);
        inMemoryTaskManager.addToSubTasks(subTask);

        assertEquals(Statuses.IN_PROGRESS, epic.getStatus());
    }
}