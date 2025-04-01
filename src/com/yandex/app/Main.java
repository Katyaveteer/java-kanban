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



        //1. Проверяем методы задачи
        // Создаем обычную задачу
        Task task1 = new Task(1, "Позвонить в офис", "Уточнить детали переезда", TaskStatus.NEW);
        manager.createTask(task1);

        Task task2 = new Task(2, "Написать письмо", "Написать коллеге письмо", TaskStatus.NEW);
        manager.createTask(task2);

        // Проверяем список всех задач
        System.out.println("Список всех задач: " + manager.getTasks());

        //меняем задачу
        manager.updateTask(new Task(2, "Написать рассказ", "тема: Мои каникулы", TaskStatus.NEW));

        // Проверяем список всех задач
        System.out.println("Список всех задач после обновления: " + manager.getTasks());

        //ищем задачу по айди
        System.out.println("задача по айди 2 : " + manager.getTaskById(2));

        //удаляем по айди
        manager.deleteTaskById(2);

        // Проверяем список всех задач
        System.out.println("Список всех задач после даления задачи 2: " + manager.getTasks());

        //удаляем все задачи
        manager.deleteAllTasks();

        // Проверяем список всех задач
        System.out.println("Список всех задач после удаления всех задач: " + manager.getTasks());



        //2. Проверяем методы подзадач
        //Создаем эпик
        Epic epic1 = new Epic(3, "Переезд", "Организация переезда в новый офис", TaskStatus.NEW);
        manager.createEpic(epic1);

        // Создаем подзадачи для эпика
        Subtask subtask1 = new Subtask(4, "Собрать коробки", "Купить и собрать коробки для переезда", TaskStatus.NEW, 3);
        Subtask subtask2 = new Subtask(5, "Упаковать компьютеры", "Упаковать всю технику", TaskStatus.NEW, 3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // список подзадач
        System.out.println("Список подзадач: " + manager.getSubtasks());

        //ищем подзадачу по айди
        System.out.println("подзадача по айди 4 : " + manager.getSubtaskById(4));

        //меняем подзадачу
        manager.updateSubtask(new Subtask(5,"Упаковать мебель","Разобрать мебель в коробки",TaskStatus.NEW,3));

        // Проверяем список всех подзадач
        System.out.println("Список всех подзадач после обновления: " + manager.getSubtasks());

        //удаляем по айди
        manager.deleteSubtaskById(5);

        // Проверяем список всех подзадач
        System.out.println("Список всех подзадач после удаления подзадачи 5: " + manager.getSubtasks());

        //удаляем все подзадачи
        manager.deleteAllSubtasks();

        // Проверяем список всех подзадач
        System.out.println("Список всех подзадач после удаления всех подзадач : " + manager.getSubtasks());



        //3. Проверяем методы эпика
        //Создаем эпик
        Epic epic2 = new Epic(6, "Написать диплом", "тема: Контроль эмоций", TaskStatus.NEW);
        manager.createEpic(epic2);

        // Создаем подзадачи для эпика
        Subtask subtask3 = new Subtask(7, "Написать 1 часть", "психология эмоций", TaskStatus.NEW, 6);
        Subtask subtask4= new Subtask(8, "Написать 2 часть", "Как контролировать эмоции", TaskStatus.NEW, 6);
        manager.createSubtask(subtask3);
        manager.createSubtask(subtask4);

        // список эпиков
        System.out.println("Список эпиков : " + manager.getEpics());
        System.out.println("Подзадачи эпика 6 : " + manager.getSubtasks());

        //ищем эпик по айди
        System.out.println("эпик по айди 6 : " + manager.getEpicById(6));
        System.out.println("Подзадачи эпика 6 : " + manager.getSubtasks());

        //меняем эпик
        manager.updateEpic(new Epic(3,"Выучить новый язык","английский",TaskStatus.NEW));

        // список эпиков
        System.out.println("Список эпиков после обновления : " + manager.getEpics());

        //удаляем по айди
        manager.deleteEpicById(6);

        // список эпиков
        System.out.println("Список эпиков после удаления 6: " + manager.getEpics());
        System.out.println("Подзадачи эпика 6 : " + manager.getSubtasks());


        //удаляем все эпики
        manager.deleteAllEpics();

        // список эпиков
        System.out.println("Список эпиков после удаления всех: " + manager.getEpics());



        //4. Проверяем дополнительные методы
        //Создаем эпик
        Epic epic3 = new Epic(9, "Пообедать", "Дома", TaskStatus.NEW);
        manager.createEpic(epic3);

        // Создаем подзадачи для эпика
        Subtask subtask5= new Subtask(10, "Купить продукты", "В пятерочке", TaskStatus.NEW, 9);
        Subtask subtask6= new Subtask(11, "Приготовить еду", "Лазанью", TaskStatus.NEW, 9);
        manager.createSubtask(subtask5);
        manager.createSubtask(subtask6);

        //Список подзадач по эпику
        System.out.println("подзадачи эпика 9 : " + manager.getSubtasksByEpicId(9));

        //обновляем 10 подзадачу с новым статусом прогресс
        manager.updateSubtask(new Subtask(10, "Купить продукты", "В пятерочке", TaskStatus.IN_PROGRESS, 9));
        //проверяем статус эпика
        System.out.println(manager.getEpics());

        //обновляем 10 подзадачу с новым статусом готово
        manager.updateSubtask(new Subtask(10, "Купить продукты", "В пятерочке", TaskStatus.DONE, 9));
        //проверяем статус эпика
        System.out.println(manager.getEpics());

        //обновляем 11 подзадачу с новым статусом готово
        manager.updateSubtask(new Subtask(11, "Приготовить еду", "Лазанью", TaskStatus.DONE, 9));
        //проверяем статус эпика
        System.out.println(manager.getEpics());


        //удаляем подзадачи
        manager.deleteSubtaskById(10);
        manager.deleteSubtaskById(11);

        //проверяем статус эпика
        System.out.println(manager.getEpics());

        System.out.println("Тест истории: ");
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