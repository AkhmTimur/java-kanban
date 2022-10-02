package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import managers.FileBackedTasksManager;
import managers.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer extends FileBackedTasksManager {
    private static final int PORT = 8080;
    private final Gson gson;
    static HttpServer server;

    public HttpTaskServer() {
        gson = new Gson();
        start();
    }

    public static void main(String[] args) {
        new HttpTaskServer();
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/tasks", new TaskHandler());
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            URI requestURI = httpExchange.getRequestURI();
            String path = requestURI.getPath();
            String method = httpExchange.getRequestMethod();

            String[] pathSplit = path.split("/");
            String dataType = null;
            int idRequest = -1;
            if(pathSplit.length > 2) {
                dataType = pathSplit[2];
                idRequest = getIdRequest(dataType, httpExchange);
            }

            if (dataType != null) {
                if (dataType.equals("task") || dataType.equals("epic") || dataType.equals("subTask")) {
                    sendDataWithoutId(dataType, idRequest, httpExchange, method,pathSplit);
                    httpExchange.sendResponseHeaders(200, 0);
                    OutputStream os = httpExchange.getResponseBody();
                    os.close();
                } else if (pathSplit.length > 3 && pathSplit[3].equals("epic")) {
                    List<SubTaskData> subTasks = getEpicSubTasks(idRequest);
                    String serialized = gson.toJson(subTasks);
                    sendSerialized(httpExchange, serialized);
                } else if (dataType.equals("history")) {
                    String serialized = gson.toJson(getHistory());
                    sendSerialized(httpExchange, serialized);
                }
            } else if (pathSplit[pathSplit.length - 1].equals("tasks")) {
                String serialized = gson.toJson(getPrioritizedTasks());
                sendSerialized(httpExchange, serialized);
            }  else {
                System.out.println("Метод " + httpExchange.getRequestMethod() + " не поддерживается");
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private ArrayList<TaskData> returnByDataType(String dataType) {
            return switch (dataType) {
                case "task" -> new ArrayList<>(getAllTasks());
                case "epic" -> new ArrayList<>(getAllEpics());
                case "subTask" -> new ArrayList<>(getAllSubTasks());
                default -> new ArrayList<>();
            };
        }

        private TaskData returnDataById(int id, String dataType) {
            return switch (dataType) {
                case "task" -> getTaskById(id);
                case "epic" -> getEpicById(id);
                case "subTask" -> getSubTaskById(id);
                default -> null;
            };
        }

        private void deleteDataById(int id, String dataType) {
            switch (dataType) {
                case "task" -> deleteTaskById(id);
                case "epic" -> deleteEpicById(id);
                case "subTask" -> deleteSubTaskById(id);
                default -> {
                }
            }
        }

        private void deleteDataByType(String dataType) {
            switch (dataType) {
                case "task" -> deleteAllTasks();
                case "epic" -> deleteAllEpics();
                case "subTask" -> deleteAllSubTasks();
                default -> {
                }
            }
        }

        private void updateData(String dataType, int idRequest, String body) {
            switch (dataType) {
                case "task":
                    for (TaskData task : getAllTasks()) {
                        if (task.getId() == idRequest) {
                            updateTask(gson.fromJson(body, TaskData.class));
                        }
                    }
                    break;
                case "epic":
                    for (EpicData epic : getAllEpics()) {
                        if (epic.getId() == idRequest) {
                            EpicData newEpic = gson.fromJson(body, EpicData.class);
                            updateEpic(newEpic);
                        }
                    }
                    break;
                case "subTask":
                    for (SubTaskData subTask : getAllSubTasks()) {
                        if (subTask.getId() == idRequest) {
                            updateEpic(gson.fromJson(body, EpicData.class));
                        }
                    }
                    break;
            }
        }

        private void addToData(String dataType, String body) {
            if (dataType.equals("task")) {
                TaskData task = gson.fromJson(body, TaskData.class);
                if(!tasks.containsKey(task.getId())) {
                    addToTasks(task);
                }
            } else if (dataType.equals("epic")) {
                EpicData epic = gson.fromJson(body, EpicData.class);
                if(!epics.containsKey(epic.getId())) {
                    addToEpics(epic);
                }
            } else {
                SubTaskData subTask = gson.fromJson(body, SubTaskData.class);
                if(!subTasks.containsKey(subTask.getId())) {
                    addToSubTasks(subTask);
                }
            }
        }

        private int getIdRequest(String dataType, HttpExchange httpExchange) {
            int idRequest = -1;
            if (dataType != null && httpExchange.getRequestURI().getRawQuery() != null) {
                idRequest = Integer.parseInt(httpExchange
                        .getRequestURI().getRawQuery().substring("id=".length()));
            }
            return idRequest;
        }

        private void sendDataWithoutId(String dataType, int idRequest, HttpExchange httpExchange, String method, String[] pathSplit) {
            if (idRequest == -1) {
                switch (method) {
                    case "GET" -> {
                        ArrayList<TaskData> tasks = returnByDataType(dataType);
                        String serialized = gson.toJson(tasks);
                        sendSerialized(httpExchange, serialized);
                    }
                    case "POST" -> {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body;
                        try {
                            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        addToData(dataType, body);
                    }
                    case "DELETE" -> deleteDataByType(dataType);
                }
            }else if (pathSplit[pathSplit.length - 1].equals("update") && method.equals("POST")) {
                InputStream inputStream = httpExchange.getRequestBody();
                String body = null;
                try {
                    body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                updateData(dataType, idRequest, body);
                String serialized = gson.toJson(getPrioritizedTasks());
                sendSerialized(httpExchange, serialized);
            } else {
                switch (method) {
                    case "GET":
                        String serialized = gson.toJson(returnDataById(idRequest, dataType));
                        sendSerialized(httpExchange, serialized);
                    case "DELETE":
                        deleteDataByType(dataType);
                        sendSerialized(httpExchange, "");
                }
            }
        }

        private void sendSerialized(HttpExchange httpExchange, String serialized) {
            try(httpExchange) {
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.write(serialized.getBytes());
                os.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void save() {
    }
}