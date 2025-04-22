package test.com.yandex.app.model;

import org.junit.jupiter.api.Test;

import static com.yandex.app.model.TaskStatus.DONE;
import static com.yandex.app.model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

;

class SubtaskTest {
    @Test
    void differentSubtaskTypesWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(1, "Sub 1", "Desc 1", NEW, 10);
        Subtask subtask2 = new Subtask(1, "Sub 2", "Desc 2", DONE, 20);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void subtaskShouldNotEqualTaskWithSameId() {
        Task task = new Task(1, "Task", "Desc", NEW);
        Subtask subtask = new Subtask(1, "Sub", "Desc", NEW, 10);

        assertNotEquals(task, subtask, "Задачи разных типов не должны быть равны");
    }
}