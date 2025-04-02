package com.yandex.app.utils;


import com.yandex.app.managers.HistoryManager;
import com.yandex.app.managers.InMemoryHistoryManager;
import com.yandex.app.managers.InMemoryTaskManager;
import com.yandex.app.managers.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}