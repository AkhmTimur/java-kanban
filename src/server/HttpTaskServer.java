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
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HTTPTaskManager httpTaskManager;
    private final Gson gson;
    static HttpServer server;

    public HttpTaskServer() {
        httpTaskManager = new HTTPTaskManager();
        gson = new Gson();
    }

    public static void main(String[] args) {
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
            String dataType = pathSplit[2];
            int idRequest = getIdRequest(dataType, httpExchange);

            if (dataType != null) {
                if (dataType.equals("task") || dataType.equals("epic") || dataType.equals("subTask")) {
                    sendDataWithoutId(dataType, idRequest, httpExchange, method,pathSplit);
                } else if (pathSplit.length > 3 && pathSplit[3].equals("epic")) {
                    List<SubTaskData> subTasks = httpTaskManager.getEpicSubTasks(idRequest);
                    String serialized = gson.toJson(subTasks);
                    sendSerialized(httpExchange, serialized);
                } else if (dataType.equals("history")) {
                    String serialized = gson.toJson(httpTaskManager.getHistory());
                    sendSerialized(httpExchange, serialized);
                }
            } else if (pathSplit[pathSplit.length - 1].equals("tasks")) {
                String serialized = gson.toJson(httpTaskManager.getPrioritizedTasks());
                sendSerialized(httpExchange, serialized);
            }  else {
                System.out.println("Метод " + httpExchange.getRequestMethod() + " не поддерживается");
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private ArrayList<TaskData> returnByDataType(String dataType) {
            httpTaskManager.loadFromServer();
            return switch (dataType) {
                case "task" -> new ArrayList<>(httpTaskManager.getAllTasks());
                case "epic" -> new ArrayList<>(httpTaskManager.getAllEpics());
                case "subTask" -> new ArrayList<>(httpTaskManager.getAllSubTasks());
                default -> new ArrayList<>();
            };
        }

        private TaskData returnDataById(int id, String dataType) {
            httpTaskManager.loadFromServer();
            return switch (dataType) {
                case "task" -> httpTaskManager.getTaskById(id);
                case "epic" -> httpTaskManager.getEpicById(id);
                case "subTask" -> httpTaskManager.getSubTaskById(id);
                default -> null;
            };
        }

        private void deleteDataById(int id, String dataType) {
            httpTaskManager.loadFromServer();
            switch (dataType) {
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

        private void updateData(String dataType, int idRequest, String body) {
            switch (dataType) {
                case "task":
                    for (TaskData task : httpTaskManager.getAllTasks()) {
                        if (task.getId() == idRequest) {
                            httpTaskManager.updateTask(gson.fromJson(body, TaskData.class));
                        }
                    }
                    break;
                case "epic":
                    for (EpicData epic : httpTaskManager.getAllEpics()) {
                        if (epic.getId() == idRequest) {
                            EpicData newEpic = gson.fromJson(body, EpicData.class);
                            httpTaskManager.updateEpic(newEpic);
                        }
                    }
                    break;
                case "subTask":
                    for (SubTaskData subTask : httpTaskManager.getAllSubTasks()) {
                        if (subTask.getId() == idRequest) {
                            httpTaskManager.updateEpic(gson.fromJson(body, EpicData.class));
                        }
                    }
                    break;
            }
        }

        private void addToData(String dataType, String body) {
            if (dataType.equals("task")) {
                httpTaskManager.addToTasks(gson.fromJson(body, TaskData.class));
            } else if (dataType.equals("epic")) {
                httpTaskManager.addToEpics(gson.fromJson(body, EpicData.class));
            } else {
                httpTaskManager.addToSubTasks(gson.fromJson(body, SubTaskData.class));
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
                    case "GET":
                        String serialized = gson.toJson(returnByDataType(dataType));
                        sendSerialized(httpExchange, serialized);
                    case "POST":
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = null;
                        try {
                            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        addToData(dataType, body);
                    case "DELETE":
                        deleteDataByType(dataType);
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
                String serialized = gson.toJson(httpTaskManager.getPrioritizedTasks());
                sendSerialized(httpExchange, serialized);
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
}