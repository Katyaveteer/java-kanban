package com.yandex.app.managers;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.Test;

import static com.yandex.app.model.TaskStatus.DONE;
import static com.yandex.app.model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private final TaskManager manager = Managers.getDefault();

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        // Создаем задачи всех типов
        int taskId = manager.createTask(new Task(1, "Title", "Description", NEW));
        int epicId = manager.createEpic(new Epic(2, "Title", "Description", NEW));
        int subtaskId = manager.createSubtask(new Subtask(3, "Sub 2", "Desc 2", DONE,epicId));

        // Проверяем поиск
        assertNotNull(manager.getTaskById(taskId));
        assertNotNull(manager.getEpicById(epicId));
        assertNotNull(manager.getSubtaskById(subtaskId));
    }

    @Test
    void shouldHandleManualAndGeneratedIds() {
        // Задача с заданным ID
        Task taskWithId = new Task(1, "Manual ID", "Desc", NEW);
        manager.createTask(taskWithId);

        // Задача с автоматическим ID
        Task autoTask = new Task("Auto ID", "Desc");
        int autoId = manager.createTask(autoTask);

        // Проверяем, что обе задачи доступны
        assertEquals(1, taskWithId.getId());
        assertNotEquals(1, autoId);
        assertNotNull(manager.getTaskById(1));
        assertNotNull(manager.getTaskById(autoId));
    }

}