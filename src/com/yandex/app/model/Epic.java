package com.yandex.app.model;

import java.util.ArrayList;


public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();

    }

    public ArrayList<Integer> getSubtaskIds() {

        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {

        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {

        subtaskIds.remove(Integer.valueOf(subtaskId));
    }
}