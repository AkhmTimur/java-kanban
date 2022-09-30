package tests.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import managers.HTTPTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {

    public static final int PORT = 8080;
    private final Gson gson = new Gson();
    HttpRequest request;
    HttpTaskServer httpTaskServer;
    HTTPTaskManager httpTaskManager;

    @BeforeEach
    void serverStart() {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        httpTaskManager = new HTTPTaskManager();
    }


    @Test
    void getTasksFromServerTest() {
        TaskData newTaskData = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        httpTaskManager.addToTasks(newTaskData);
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/task");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        ArrayList<TaskData> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<TaskData>>() {
        }.getType());
        assertEquals(1, tasks.size());
    }

    @Test
    void getTaskByIdFromServerTest() {
        TaskData newTaskData = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        httpTaskManager.addToTasks(newTaskData);
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task?id=0");
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            TaskData task = gson.fromJson(response.body(), TaskData.class);
            assertEquals(0, task.getId());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postTaskToServerTest() {
        TaskData newTaskData = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        httpTaskManager.addToTasks(newTaskData);
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            String json = gson.toJson(newTaskData, TaskData.class);
            request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(uri)
                    .build();
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            URI url = URI.create("http://localhost:8080/tasks/task");
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(url)
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
    void deletePostByIdTest() {
        TaskData newTaskData = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        httpTaskManager.addToTasks(newTaskData);
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            String json = gson.toJson(newTaskData, TaskData.class);
            request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(uri)
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ArrayList<TaskData> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<TaskData>>() {
            }.getType());
            assertEquals(1, tasks.size());

            URI url = URI.create("http://localhost:8080/tasks/task?id=" + newTaskData.getId());
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(url)
                    .build();
            HttpClient.newHttpClient().send(getRequest, HttpResponse.BodyHandlers.ofString());


            URI uri1 = URI.create("http://localhost:8080/tasks/task");
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri1)
                    .build();
            HttpResponse<String> responseAfterDelete = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ArrayList<TaskData> tasksAfterDelete = gson.fromJson(responseAfterDelete.body(), new TypeToken<ArrayList<TaskData>>() {
            }.getType());
            assertEquals(0, tasksAfterDelete.size());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteAllTasksTest() {
        TaskData newTaskData = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        TaskData newTaskData1 = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        httpTaskManager.addToTasks(newTaskData);
        httpTaskManager.addToTasks(newTaskData1);
        newTaskData1.setDuration(120);
        newTaskData1.setStartDate(2022, 2, 25);
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            String json = gson.toJson(newTaskData, TaskData.class);
            request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(uri)
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ArrayList<TaskData> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<TaskData>>() {
            }.getType());
            assertEquals(2, tasks.size());

            URI url = URI.create("http://localhost:8080/tasks/task" + newTaskData.getId());
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(url)
                    .build();
            HttpClient.newHttpClient().send(getRequest, HttpResponse.BodyHandlers.ofString());


            URI uri1 = URI.create("http://localhost:8080/tasks/task");
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri1)
                    .build();
            HttpResponse<String> responseAfterDelete = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ArrayList<TaskData> tasksAfterDelete = gson.fromJson(responseAfterDelete.body(), new TypeToken<ArrayList<TaskData>>() {
            }.getType());
            assertEquals(0, tasksAfterDelete.size());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getEpicSubtasksTest() {
        EpicData epic = new EpicData("test", "testDesc", Statuses.NEW);
        httpTaskManager.addToEpics(epic);
        SubTaskData subTaskData = new SubTaskData("testSubtask", "desc");
        SubTaskData subTaskData1 = new SubTaskData("testSubtask~1", "desc");
        subTaskData.setEpicId(epic.getId());
        subTaskData1.setEpicId(epic.getId());
        subTaskData.setDuration(120);
        subTaskData.setStartDate(2022, 2, 24);
        subTaskData1.setDuration(120);
        subTaskData1.setStartDate(2022, 2, 25);
        httpTaskManager.addToTasks(subTaskData);
        httpTaskManager.addToTasks(subTaskData1);

        try {
            URI uri = URI.create("http://localhost:8080/tasks/subtask/epic?id=0");
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ArrayList<TaskData> epicSubTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<TaskData>>() {
            }.getType());
            assertEquals(2, epicSubTasks.size());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getHistoryTest() {
        EpicData epic = new EpicData("test", "testDesc", Statuses.NEW);
        httpTaskManager.addToEpics(epic);
        SubTaskData subTaskData = new SubTaskData("testSubtask", "desc");
        subTaskData.setEpicId(epic.getId());
        subTaskData.setDuration(120);
        subTaskData.setStartDate(2022, 2, 24);
        httpTaskManager.addToSubTasks(subTaskData);
        httpTaskManager.getEpicById(epic.getId());
        httpTaskManager.getSubTaskById(subTaskData.getId());

        try {
            URI uri = URI.create("http://localhost:8080/tasks/history");
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            List<TaskData> history = gson.fromJson(response.body(), new TypeToken<List<TaskData>>() {
            }.getType());
            assertEquals(2, history.size());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getPrioritizedTasksTest() {
        TaskData newTaskData = new TaskData("test", "desc");
        newTaskData.setDuration(120);
        newTaskData.setStartDate(2022, 2, 24);
        httpTaskManager.addToTasks(newTaskData);
        TaskData newTaskData1 = new TaskData("test", "desc");
        newTaskData1.setDuration(120);
        newTaskData1.setStartDate(2022, 2, 25);
        httpTaskManager.addToTasks(newTaskData1);

        try {
            URI uri = URI.create("http://localhost:8080/tasks");
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            List<TaskData> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<TaskData>>() {
            }.getType());
            assertEquals(newTaskData1, prioritizedTasks.get(0));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
