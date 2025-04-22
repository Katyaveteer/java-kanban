package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.utils.Managers;
import com.yandex.app.managers.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // Создаем задачи
        Task task1 = new Task("Задача 1", "Описание");
        Task task2 = new Task("Задача 2", "Описание");
        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);

        // Создаем эпики
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Epic epic2 = new Epic("Эпик 2", "Описание");
        int epic1Id = manager.createEpic(epic1);
        int epic2Id = manager.createEpic(epic2);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1Id);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", epic1Id);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание", epic1Id);
        int subtask1Id = manager.createSubtask(subtask1);
        int subtask2Id = manager.createSubtask(subtask2);
        int subtask3Id = manager.createSubtask(subtask3);

        // Запрашиваем задачи в разном порядке
        manager.getTaskById(task1Id);
        manager.getEpicById(epic1Id);
        manager.getSubtaskById(subtask1Id);
        manager.getTaskById(task2Id);
        manager.getTaskById(task1Id); // Дубликат

        printHistory(manager); // История без дублей

        // Удаляем задачу из истории
        manager.deleteTaskById(task1Id);
        printHistory(manager);

        // Удаляем эпик с подзадачами
        manager.deleteEpicById(epic1Id);
        printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}