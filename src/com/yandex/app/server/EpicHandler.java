package com.yandex.app.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.Epic;
import com.yandex.app.server.adapters.DurationAdapter;
import com.yandex.app.server.adapters.LocalDateAdapter;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager) {

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
                        List<Epic> epics = manager.getEpics();
                        sendText(h, gson.toJson(epics));
                        h.close();
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        Epic epic = manager.getEpicById(id);
                        sendText(h, gson.toJson(epic));
                        h.close();
                    }
                    break;
                case "POST":

                    String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    if (body.isBlank()) {
                        sendNotFound(h, "Пустое тело запроса");
                        return;
                    }
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (epic.getId() != 0) {
                        manager.updateEpic(epic);
                        h.sendResponseHeaders(201, 0);
                    } else {
                        manager.createEpic(epic);
                        String responseJson = gson.toJson(epic);
                        byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
                        h.sendResponseHeaders(201, responseBytes.length);
                        h.getResponseBody().write(responseBytes);
                    }
                    h.close();
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.deleteAllEpics();
                        sendText(h, "Все эпики удалены");
                        h.close();
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        manager.deleteEpicById(id);
                        sendText(h, "Эпик: " + id + "удален.");
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
