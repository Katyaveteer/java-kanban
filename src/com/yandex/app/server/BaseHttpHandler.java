package com.yandex.app.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {


    protected void sendText(HttpExchange h, String message) throws IOException {
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String message) throws IOException {
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");

        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(message.getBytes());
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String message) throws IOException {
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(message.getBytes());
        h.close();
    }

    protected void sendServerError(HttpExchange h, Exception e) throws IOException {
        String message = "Internal Server Error: " + e.getMessage();
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}


