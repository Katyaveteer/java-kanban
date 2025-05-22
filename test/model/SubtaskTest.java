package model;

import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.yandex.app.model.TaskStatus.DONE;
import static com.yandex.app.model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class SubtaskTest {
    @Test
    void differentSubtaskTypesWithSameIdShouldBeEqual() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        Subtask subtask1 = new Subtask(1, "Sub 1", "Desc 1", 1, NEW, Duration.ofHours(1), startTime);
        Subtask subtask2 = new Subtask(1, "Sub 2", "Desc 2", 1, DONE, Duration.ofHours(1), startTime);


        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void subtaskShouldNotEqualTaskWithSameId() {
        Task task = new Task("Task", "Desc", NEW);
        Subtask subtask = new Subtask(1, "Sub", "Desc", 1, NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(2));

        assertNotEquals(task, subtask, "Задачи разных типов не должны быть равны");
    }
}