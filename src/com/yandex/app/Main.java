package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskStatus;
import com.yandex.app.service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();


        //1. Проверяем методы задачи
        // Создаем обычную задачу
        Task task1 = new Task(1, "Позвонить в офис", "Уточнить детали переезда", TaskStatus.NEW);
        taskManager.createTask(task1);

        Task task2 = new Task(2, "Написать письмо", "Написать коллеге письмо", TaskStatus.NEW);
        taskManager.createTask(task2);

        // Проверяем список всех задач
        System.out.println("Список всех задач: " + taskManager.getTasks());

        //меняем задачу
        taskManager.updateTask(new Task(2, "Написать рассказ", "тема: Мои каникулы", TaskStatus.NEW));

        // Проверяем список всех задач
        System.out.println("Список всех задач после обновления: " + taskManager.getTasks());

        //ищем задачу по айди
        System.out.println("задача по айди 2 : " + taskManager.getTaskById(2));

        //удаляем по айди
        taskManager.deleteTaskById(2);

        // Проверяем список всех задач
        System.out.println("Список всех задач после даления задачи 2: " + taskManager.getTasks());

        //удаляем все задачи
        taskManager.deleteAllTasks();

        // Проверяем список всех задач
        System.out.println("Список всех задач после удаления всех задач: " + taskManager.getTasks());



        //2. Проверяем методы подзадач
        //Создаем эпик
        Epic epic1 = new Epic(3, "Переезд", "Организация переезда в новый офис", TaskStatus.NEW);
        taskManager.createEpic(epic1);

        // Создаем подзадачи для эпика
        Subtask subtask1 = new Subtask(4, "Собрать коробки", "Купить и собрать коробки для переезда", TaskStatus.NEW, 3);
        Subtask subtask2 = new Subtask(5, "Упаковать компьютеры", "Упаковать всю технику", TaskStatus.NEW, 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // список подзадач
        System.out.println("Список подзадач: " + taskManager.getSubtasks());

        //ищем подзадачу по айди
        System.out.println("подзадача по айди 4 : " + taskManager.getSubtaskById(4));

        //меняем подзадачу
        taskManager.updateSubtask(new Subtask(5,"Упаковать мебель","Разобрать мебель в коробки",TaskStatus.NEW,3));

        // Проверяем список всех подзадач
        System.out.println("Список всех подзадач после обновления: " + taskManager.getSubtasks());

        //удаляем по айди
        taskManager.deleteSubtaskById(5);

        // Проверяем список всех подзадач
        System.out.println("Список всех подзадач после удаления подзадачи 5: " + taskManager.getSubtasks());

        //удаляем все подзадачи
        taskManager.deleteAllSubtasks();

        // Проверяем список всех подзадач
        System.out.println("Список всех подзадач после удаления всех подзадач : " + taskManager.getSubtasks());



        //3. Проверяем методы эпика
        //Создаем эпик
        Epic epic2 = new Epic(6, "Написать диплом", "тема: Контроль эмоций", TaskStatus.NEW);
        taskManager.createEpic(epic2);

        // Создаем подзадачи для эпика
        Subtask subtask3 = new Subtask(7, "Написать 1 часть", "психология эмоций", TaskStatus.NEW, 6);
        Subtask subtask4= new Subtask(8, "Написать 2 часть", "Как контролировать эмоции", TaskStatus.NEW, 6);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);

        // список эпиков
        System.out.println("Список эпиков : " + taskManager.getEpics());
        System.out.println("Подзадачи эпика 6 : " + taskManager.getSubtasks());

        //ищем эпик по айди
        System.out.println("эпик по айди 6 : " + taskManager.getEpicById(6));
        System.out.println("Подзадачи эпика 6 : " + taskManager.getSubtasks());

        //меняем эпик
        taskManager.updateEpic(new Epic(3,"Выучить новый язык","английский",TaskStatus.NEW));

        // список эпиков
        System.out.println("Список эпиков после обновления : " + taskManager.getEpics());

        //удаляем по айди
        taskManager.deleteEpicById(6);

        // список эпиков
        System.out.println("Список эпиков после удаления 6: " + taskManager.getEpics());
        System.out.println("Подзадачи эпика 6 : " + taskManager.getSubtasks());


        //удаляем все эпики
        taskManager.deleteAllEpics();

        // список эпиков
        System.out.println("Список эпиков после удаления всех: " + taskManager.getEpics());



        //4. Проверяем дополнительные методы
        //Создаем эпик
        Epic epic3 = new Epic(9, "Пообедать", "Дома", TaskStatus.NEW);
        taskManager.createEpic(epic3);

        // Создаем подзадачи для эпика
        Subtask subtask5= new Subtask(10, "Купить продукты", "В пятерочке", TaskStatus.NEW, 9);
        Subtask subtask6= new Subtask(11, "Приготовить еду", "Лазанью", TaskStatus.NEW, 9);
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);

        //Список подзадач по эпику
        System.out.println("подзадачи эпика 9 : " + taskManager.getSubtasksByEpicId(9));

        //обновляем 10 подзадачу с новым статусом прогресс
        taskManager.updateSubtask(new Subtask(10, "Купить продукты", "В пятерочке", TaskStatus.IN_PROGRESS, 9));
        //проверяем статус эпика
        System.out.println(taskManager.getEpics());

        //обновляем 10 подзадачу с новым статусом готово
        taskManager.updateSubtask(new Subtask(10, "Купить продукты", "В пятерочке", TaskStatus.DONE, 9));
        //проверяем статус эпика
        System.out.println(taskManager.getEpics());

        //обновляем 11 подзадачу с новым статусом готово
        taskManager.updateSubtask(new Subtask(11, "Приготовить еду", "Лазанью", TaskStatus.DONE, 9));
        //проверяем статус эпика
        System.out.println(taskManager.getEpics());


        //удаляем подзадачи
        taskManager.deleteSubtaskById(10);
        taskManager.deleteSubtaskById(11);

        //проверяем статус эпика
        System.out.println(taskManager.getEpics());


    }
}