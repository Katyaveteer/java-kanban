package com.yandex.app;

import com.yandex.app.managers.FileBackedTaskManager;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.TaskStatus;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks_backup.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Создаем задачи
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание", TaskStatus.NEW);
        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);

        // Создаем эпики
        Epic epic1 = new Epic("Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание", TaskStatus.NEW);
        int epic1Id = manager.createEpic(epic1);
        int epic2Id = manager.createEpic(epic2);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic1Id);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, epic1Id);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание", TaskStatus.NEW, epic1Id);
        int subtask1Id = manager.createSubtask(subtask1);
        int subtask2Id = manager.createSubtask(subtask2);
        int subtask3Id = manager.createSubtask(subtask3);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Проверяем, что все задачи восстановлены
        System.out.println("Оригинальный менеджер:");
        System.out.println("Tasks: " + manager.getTasks());
        System.out.println("Epics: " + manager.getEpics());
        System.out.println("Subtasks: " + manager.getSubtasks());

        System.out.println("\nЗагруженный менеджер:");
        System.out.println("Tasks: " + loadedManager.getTasks());
        System.out.println("Epics: " + loadedManager.getEpics());
        System.out.println("Subtasks: " + loadedManager.getSubtasks());

    }
}