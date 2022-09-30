package tests.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataClasses.TaskData;
import managers.HTTPTaskManager;
import managers.KVServer;
import managers.KVTaskClient;
import org.junit.jupiter.api.AfterEach;
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
    KVServer kvServer;
    String urlToServer;

    @BeforeEach
    void beforeEach() {
        urlToServer = "http://localhost:8078";
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson = new Gson();
        httpTaskManager = new HTTPTaskManager(urlToServer);

        newTaskData = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
    }

    @Test
    void taskSaveTest() {
        httpTaskManager.addToTasks(newTaskData);

        try {
            URI uri = URI.create(urlToServer + "/load/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ArrayList<TaskData> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<TaskData>>() {
            }.getType());
            assertEquals(1, tasks.size());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void loadFromServer() {
        httpTaskManager.addToTasks(newTaskData);

        httpTaskManager.loadFromServer();

        assertEquals(1, httpTaskManager.getAllTasks().size());
    }

    @AfterEach
    void stopServer() {
        kvServer.stop();
    }
}
