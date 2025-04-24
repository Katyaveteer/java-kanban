package com.yandex.app.managers;

import com.yandex.app.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();


}
