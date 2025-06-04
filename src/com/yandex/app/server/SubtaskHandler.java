package com.yandex.app.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.Subtask;
import com.yandex.app.server.adapters.DurationAdapter;
import com.yandex.app.server.adapters.LocalDateAdapter;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager) {

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
                        List<Subtask> subtasks = manager.getSubtasks();
                        sendText(h, gson.toJson(subtasks));
                        h.close();
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        Subtask subtask = manager.getSubtaskById(id);
                        sendText(h, gson.toJson(subtask));
                        h.close();
                    }
                    break;
                case "POST":
                    String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    if (subtask.getId() != 0) {
                        manager.updateSubtask(subtask);
                    } else {
                        manager.createSubtask(subtask);
                    }
                    h.sendResponseHeaders(201, 0);
                    h.close();
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.deleteAllSubtasks();
                        sendText(h, "Все подзадачи удалены");
                        h.close();
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        manager.deleteSubtaskById(id);
                        sendText(h, "Подзадача: " + id + " удалена.");
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

