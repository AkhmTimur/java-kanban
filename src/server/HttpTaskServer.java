package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import managers.HTTPTaskManager;

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
    private final Gson gson;
    HttpServer server;
    private final HTTPTaskManager httpTaskManager;

    public HttpTaskServer(HTTPTaskManager httpTaskManager) {
        gson = new Gson();
        this.httpTaskManager = httpTaskManager;
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            server.start();
            System.out.println("Запускаем сервер на " + PORT + " порту");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/tasks", new TaskHandler());
    }

    public static void main(String[] args) {
    }

    public void stop() {
        server.stop(0);
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
            if (pathSplit.length > 2) {
                dataType = pathSplit[2];
                idRequest = getIdRequest(dataType, httpExchange);
            }

            if (dataType != null) {
                if (dataType.equals("task") || dataType.equals("epic") || dataType.equals("subTask")) {
                    sendDataWithoutId(dataType, idRequest, httpExchange, method);
                    httpExchange.sendResponseHeaders(200, 0);
                    OutputStream os = httpExchange.getResponseBody();
                    os.close();
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
            } else {
                System.out.println("Метод " + httpExchange.getRequestMethod() + " не поддерживается");
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private ArrayList<TaskData> returnByDataType(String dataType) {
            return switch (dataType) {
                case "task" -> new ArrayList<>(httpTaskManager.getAllTasks());
                case "epic" -> new ArrayList<>(httpTaskManager.getAllEpics());
                case "subTask" -> new ArrayList<>(httpTaskManager.getAllSubTasks());
                default -> new ArrayList<>();
            };
        }

        private TaskData returnDataById(int id, String dataType) {
            return switch (dataType) {
                case "task" -> httpTaskManager.getTaskById(id);
                case "epic" -> httpTaskManager.getEpicById(id);
                case "subTask" -> httpTaskManager.getSubTaskById(id);
                default -> null;
            };
        }

        private void deleteDataById(int id, String dataType) {
            switch (dataType) {
                case "task" -> httpTaskManager.deleteTaskById(id);
                case "epic" -> httpTaskManager.deleteEpicById(id);
                case "subTask" -> httpTaskManager.deleteSubTaskById(id);
                default -> {
                }
            }
        }

        private void deleteDataByType(String dataType) {
            switch (dataType) {
                case "task" -> httpTaskManager.deleteAllTasks();
                case "epic" -> httpTaskManager.deleteAllEpics();
                case "subTask" -> httpTaskManager.deleteAllSubTasks();
                default -> {
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

        private void sendDataWithoutId(String dataType, int idRequest, HttpExchange httpExchange, String method) {
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
                        updateData(dataType, body);
                    }
                    case "DELETE" -> deleteDataByType(dataType);
                }
            } else {
                switch (method) {
                    case "GET" -> {
                        String serialized = gson.toJson(returnDataById(idRequest, dataType));
                        sendSerialized(httpExchange, serialized);
                    }
                    case "DELETE" -> {
                        deleteDataById(idRequest, dataType);
                        sendSerialized(httpExchange, "");
                    }
                }
            }
        }

        private void updateData(String dataType, String body) {
            switch (dataType) {
                case "task" -> {
                    TaskData deSerialized = gson.fromJson(body, TaskData.class);
                    if(deSerialized.getId() != -1) {
                        httpTaskManager.updateTask(deSerialized);
                    } else {
                        httpTaskManager.addToTasks(deSerialized);
                    }

                }
                case "epic" -> {
                    EpicData deSerialized = gson.fromJson(body, EpicData.class);
                    if(deSerialized.getId() != -1) {
                        httpTaskManager.updateEpic(deSerialized);
                    } else {
                        httpTaskManager.addToEpics(deSerialized);
                    }
                }
                case "subTask" -> {
                    SubTaskData deSerialized = gson.fromJson(body, SubTaskData.class);
                    if(deSerialized.getId() != -1) {
                        httpTaskManager.updateSubTask(deSerialized);
                    } else {
                        httpTaskManager.addToSubTasks(deSerialized);
                    }
                }
            }
        }

        private void sendSerialized(HttpExchange httpExchange, String serialized) {
            try (httpExchange) {
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.write(serialized.getBytes());
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}