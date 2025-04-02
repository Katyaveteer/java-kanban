package com.yandex.app.managers;

import com.yandex.app.model.Task;

import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final ArrayList<Task> history = new ArrayList<>();


    @Override
    public void add(Task task) {
        if (task != null) {
            Task oldTask = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
            if (history.size() >= MAX_HISTORY_SIZE) {
                history.removeFirst();
            }
            history.add(oldTask);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {

        return history;
    }
}
