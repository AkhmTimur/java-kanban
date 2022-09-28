package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import managers.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HTTPTaskManager httpTaskManager;
    static Gson gson = new Gson();
    static KVTaskClient client;
    public HttpTaskServer() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        final String urlToKVServer = "http://localhost:8078";

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpTaskManager = new HTTPTaskManager(urlToKVServer);
        httpServer.start();
        new KVServer().start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");


        TaskData newTaskData1 = new TaskData("test`1", "desc`1");
        newTaskData1.setDuration(240);
        newTaskData1.setStartDate(2022, 3, 24);
        httpTaskManager.addToTasks(newTaskData1);
    }

    public static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            URI requestURI = httpExchange.getRequestURI();
            String path = requestURI.getPath();
            String method = httpExchange.getRequestMethod();

            String[] pathSplit = path.split("/");
            String dataType = pathSplit[2];
            int idRequest = -1;

            if (dataType != null && httpExchange.getRequestURI().getRawQuery() != null) {
                idRequest = Integer.parseInt(httpExchange
                        .getRequestURI().getRawQuery().substring("id=".length()));
            }
            if (dataType != null) {
                if (dataType.equals("task") || dataType.equals("epic") || dataType.equals("subTask")) {
                    if (idRequest == -1) {
                        switch (method) {
                            case "GET":
                                String serialized = gson.toJson(returnByDataType(dataType));
                                sendSerialized(httpExchange, serialized);
                            case "POST":
                                InputStream inputStream = httpExchange.getRequestBody();
                                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                if (dataType.equals("task")) {
                                    httpTaskManager.addToTasks(gson.fromJson(body, TaskData.class));
                                } else if (dataType.equals("epic")) {
                                    httpTaskManager.addToTasks(gson.fromJson(body, EpicData.class));
                                } else {
                                    httpTaskManager.addToTasks(gson.fromJson(body, SubTaskData.class));
                                }
                            case "DELETE":
                                deleteDataByType(dataType);
                        }
                    } else {
                        switch (method) {
                            case "GET":
                                String serialized = gson.toJson(returnDataById(idRequest, dataType));
                                sendSerialized(httpExchange, serialized);
                            case "DELETE":
                                deleteDataById(idRequest, dataType);
                                sendSerialized(httpExchange, "");
                        }
                    }
                } else if (pathSplit.length > 3 && pathSplit[3].equals("epic")) {
                    ArrayList<SubTaskData> subTasks = new ArrayList<>();
                    for (Integer subTaskId : httpTaskManager.getEpicById(idRequest).getSubTaskIdList()) {
                        ArrayList<SubTaskData> allSubTasks = httpTaskManager.getAllSubTasks();
                        for (SubTaskData subTask : allSubTasks) {
                            if (subTask.getId() == subTaskId) {
                                subTasks.add(subTask);
                            }
                        }
                    }
                    String serialized = gson.toJson(subTasks);
                    sendSerialized(httpExchange, serialized);
                } else if (dataType.equals("history")) {
                    String serialized = gson.toJson(httpTaskManager.getHistory());
                    sendSerialized(httpExchange, serialized);
                }
            } else if (pathSplit[pathSplit.length - 1].equals("tasks")) {
                String serialized = gson.toJson(httpTaskManager.getPrioritizedTasks());
                sendSerialized(httpExchange, serialized);
            }

        }

        private ArrayList<TaskData> returnByDataType(String dataType) {
            httpTaskManager.loadFromServer();
            switch (dataType) {
                case "task":
                    return new ArrayList<>(httpTaskManager.getAllTasks());
                case "epic":
                    return new ArrayList<>(httpTaskManager.getAllEpics());
                case "subTask":
                    return new ArrayList<>(httpTaskManager.getAllSubTasks());
                default:
                    return new ArrayList<>();
            }
        }

        private TaskData returnDataById(int id, String dataType) {
            httpTaskManager.loadFromServer();
            switch (dataType) {
                case "task":
                    return httpTaskManager.getTaskById(id);
                case "epic":
                    return httpTaskManager.getEpicById(id);
                case "subTask":
                    return httpTaskManager.getSubTaskById(id);
                default:
                    return null;
            }
        }

        private void deleteDataById(int id, String dataType) {
            httpTaskManager.loadFromServer();
            switch (dataType){
                case "task":
                    httpTaskManager.deleteTaskById(id);
                case "epic":
                    httpTaskManager.deleteEpicById(id);
                case "subTask":
                    httpTaskManager.deleteSubTaskById(id);
                default:
            }
        }

        private void deleteDataByType(String dataType) {
            switch (dataType) {
                case "task":
                    httpTaskManager.deleteAllTasks();
                case "epic":
                    httpTaskManager.deleteAllEpics();
                case "subTask":
                    httpTaskManager.deleteAllSubTasks();
                default:
            }
        }

        private void sendSerialized(HttpExchange httpExchange, String serialized) throws IOException {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(serialized.getBytes());
            os.close();
        }
    }
}