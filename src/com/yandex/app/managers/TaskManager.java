package com.yandex.app.managers;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    int createTask(Task task);

    ArrayList<Task> getTasks();

    //Task getTask(int id);

    Task getTaskById(int id);

    void updateTask(Task task);

    void deleteTaskById(int id);

    void deleteAllTasks();

    // Методы для подзадач
    int createSubtask(Subtask subtask);

    ArrayList<Subtask> getSubtasks();

    Subtask getSubtaskById(int id);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

    // Методы для эпиков
    int createEpic(Epic epic);

    ArrayList<Epic> getEpics();

    Epic getEpicById(int id);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    void deleteAllEpics();

    // Дополнительные методы
    ArrayList<Subtask> getSubtasksByEpicId(int epicId);
    ArrayList<Task> getHistory();

}
