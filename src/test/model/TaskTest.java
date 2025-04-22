package model;

import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.Task;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.Test;

import static com.yandex.app.model.TaskStatus.IN_PROGRESS;
import static com.yandex.app.model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Title", "Description", NEW);
        Task task2 = new Task(1, "Different Title", "Different Desc", IN_PROGRESS);
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void taskShouldRemainUnchangedWhenAddedToManager() {
        TaskManager manager = Managers.getDefault();
        Task original = new Task(1, "Original", "Desc", NEW);

        int taskId = manager.createTask(original);
        Task fromManager = manager.getTaskById(taskId);

        // Проверяем все поля
        assertEquals(original.getId(), fromManager.getId());
        assertEquals(original.getTitle(), fromManager.getTitle());
        assertEquals(original.getDescription(), fromManager.getDescription());
        assertEquals(original.getStatus(), fromManager.getStatus());
    }
}

