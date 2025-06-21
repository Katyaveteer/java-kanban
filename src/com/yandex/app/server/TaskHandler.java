package com.yandex.app.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.Task;
import com.yandex.app.server.adapters.DurationAdapter;
import com.yandex.app.server.adapters.LocalDateAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String query = h.getRequestURI().getQuery();


            switch (method) {
                case "GET":
                    if (query == null) {
                        List<Task> tasks = manager.getTasks();
                        sendText(h, gson.toJson(tasks));
                        h.close();
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        Task task = manager.getTaskById(id);
                        sendText(h, gson.toJson(task));
                        h.close();
                    }
                    break;
                case "POST":
                    String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getId() != 0) {
                        manager.updateTask(task);
                        h.sendResponseHeaders(201, 0);
                    } else {
                        manager.createTask(task);
                        String responseJson = gson.toJson(task);
                        byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
                        h.sendResponseHeaders(201, responseBytes.length);
                        h.getResponseBody().write(responseBytes);
                    }

                    h.close();
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.deleteAllTasks();
                        sendText(h, "Все задачи удалены");
                        h.close();
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        manager.deleteTaskById(id);
                        sendText(h, "Задача: " + id + " удалена.");
                        h.close();
                    }
                    break;
                default:
                    sendNotFound(h, "Метод не поддерживается");
            }

        } catch (NotFoundException e) {
            sendNotFound(h, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendHasInteractions(h, e.getMessage());
        } catch (Exception e) {
            sendServerError(h, e);
        }
    }


}

