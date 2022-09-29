package tests.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import managers.HTTPTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest {
    Gson gson;
    HTTPTaskManager httpTaskManager;
    TaskData newTaskData;

    @BeforeEach
    void beforeEach() {
        gson = new Gson();
        httpTaskManager = new HTTPTaskManager("https://localhost:8078");

        newTaskData = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
    }

    @Test
    void taskSaveTest() {
        httpTaskManager.addToTasks(newTaskData);
        httpTaskManager.save();
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ArrayList<TaskData> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<TaskData>>() {}.getType());
            assertEquals(1, tasks.size());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void loadFromServer() {
        httpTaskManager.addToTasks(newTaskData);
        EpicData epic = new EpicData("test", "testDesc", Statuses.NEW);
        httpTaskManager.addToEpics(epic);
        SubTaskData subTaskData = new SubTaskData("testSubtask", "desc");
        subTaskData.setEpicId(epic.getId());
        httpTaskManager.addToSubTasks(subTaskData);
        subTaskData.setDuration(120);
        subTaskData.setStartDate(2022, 2, 24);
        httpTaskManager.save();
        httpTaskManager.deleteAllTasks();
        httpTaskManager.deleteAllEpics();

        httpTaskManager.loadFromServer();
        assertEquals(1, httpTaskManager.getAllTasks().size());
        assertEquals(1, httpTaskManager.getAllEpics().size());
        assertEquals(1, httpTaskManager.getAllSubTasks().size());
    }


}
