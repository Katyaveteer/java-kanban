package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskStatus;
import com.yandex.app.utils.Managers;
import com.yandex.app.managers.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        // Создаем задачи
        Task task1 = new Task(1, "Позвонить в офис", "Уточнить детали переезда", TaskStatus.NEW);
        manager.createTask(task1);

        Task task2 = new Task(2, "Написать письмо", "Написать коллеге письмо", TaskStatus.NEW);
        manager.createTask(task2);

        //Создаем эпик
        Epic epic1 = new Epic(3, "Переезд", "Организация переезда в новый офис", TaskStatus.NEW);
        manager.createEpic(epic1);

        // Создаем подзадачи для эпика
        Subtask subtask1 = new Subtask(4, "Собрать коробки", "Купить и собрать коробки для переезда", TaskStatus.NEW, 3);
        Subtask subtask2 = new Subtask(5, "Упаковать компьютеры", "Упаковать всю технику", TaskStatus.NEW, 3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);


        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}