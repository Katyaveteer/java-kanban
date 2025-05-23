package com.yandex.app;

import com.yandex.app.managers.FileBackedTaskManager;
import com.yandex.app.model.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        // файл для постоянного хранилища
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // 1) Создаём обычную задачу
        Task task = new Task(
                "Задача",
                "Описание задачи", TaskStatus.NEW,
                Duration.ofMinutes(90),
                LocalDateTime.of(2025, 5, 23, 10, 0));
        int taskId = manager.createTask(task);

        // 2) Создаём эпик (эпики сами по себе не имеют времени)
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = manager.createEpic(epic);

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        int epicId2 = manager.createEpic(epic2);


        // 3) Создаём подзадачи для этого эпика
        Subtask sub1 = new Subtask(
                1,
                "Подзадача1",
                "Описание подзадачи1",
                epicId,
                TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(120),
                LocalDateTime.of(2025, 5, 23, 10, 0)
        );
        manager.createSubtask(sub1);

        Subtask sub2 = new Subtask(
                2,
                "Подзадача2",
                "Описание подзадачи2",
                epicId,
                TaskStatus.NEW,
                Duration.ofMinutes(180),
                LocalDateTime.of(2025, 5, 23, 14, 0)
        );
        manager.createSubtask(sub2);

        Subtask sub3 = new Subtask(
                3,
                "Подзадача3",
                "Описание подзадачи3",
                epicId,
                TaskStatus.NEW,
                Duration.ofMinutes(90),
                LocalDateTime.of(2025, 5, 24, 9, 0)
        );
        manager.createSubtask(sub3);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");


        // 4) Получаем и выводим обычную задачу
        System.out.println("\n Обычная задача");
        manager.getTaskById(taskId)
                .ifPresentOrElse(
                        t -> {
                            String start = t.getStartTime().format(fmt);
                            String end = t.getEndTime().format(fmt);
                            System.out.println(t.getTitle() + ": " + start + " → " + end);
                        },
                        () -> System.out.println("Задача не найдена")
                );

        // 5) Получаем и выводим эпик вместе с рассчитанным временем и длительностью
        System.out.println("\n Эпик");
        Optional<Epic> optEpic = manager.getEpicById(epicId);
        if (optEpic.isPresent()) {
            Epic e = optEpic.get();
            System.out.println(e);
            System.out.println("Длительность эпика: " + e.getDuration().toMinutes() + " мин");
            System.out.println("Начало эпика:    " + e.getStartTime().format(fmt));
            System.out.println("Конец   эпика:   " + e.getEndTime().format(fmt));
        } else {
            System.out.println("Эпик не найден");
        }

        System.out.println("\n Эпик");
        Optional<Epic> optEpic2 = manager.getEpicById(epicId2);
        if (optEpic2.isPresent()) {
            Epic e = optEpic2.get();
            System.out.println(e);
            System.out.println("Длительность эпика: " + e.getDuration().toMinutes() + " мин");
            System.out.println("Начало эпика:    " + e.getStartTime().format(fmt));
            System.out.println("Конец   эпика:   " + e.getEndTime().format(fmt));
        } else {
            System.out.println("Эпик не найден");
        }

        // 6) Выводим все подзадачи эпика
        System.out.println("\n Подзадачи эпика ");
        manager.getSubtasksByEpicId(epicId)
                .forEach(System.out::println);

        // 7) Выводим историю просмотров
        System.out.println("\n История просмотров ");
        manager.getHistory().forEach(System.out::println);

        // 8) Выводим задачи в порядке приоритета
        System.out.println("\nПриоритетный список задач");
        for (Task t : manager.getPrioritizedTasks()) {
            System.out.printf(
                    "[%s] %s — %s → %s%n",
                    t.getType(), t.getTitle(),
                    t.getStartTime().format(fmt), t.getEndTime().format(fmt)
            );
        }
    }
}
