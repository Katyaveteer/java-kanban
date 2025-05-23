package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;



    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
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

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }


    public LocalDateTime getEndTime() {

        if (subtaskIds.isEmpty()) {
            return LocalDateTime.now().plus(getDuration());
        }
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {

        this.endTime = endTime;
    }

    @Override
    public Duration getDuration() {
        if (subtaskIds.isEmpty()) {
            return Duration.ZERO;
        }
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtaskIds.isEmpty()) {
            return LocalDateTime.now();
        }
        return super.getStartTime();
    }


}