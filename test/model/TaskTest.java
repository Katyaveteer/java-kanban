package model;

import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskStatus;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

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
        Task original = new Task("Original", "Desc", NEW);

        int taskId = manager.createTask(original);
        Optional<Task> fromManager = manager.getTaskById(taskId);
        assertTrue(fromManager.isPresent());
        assertEquals(taskId, fromManager.get().getId());
        assertEquals(original.getTitle(), fromManager.get().getTitle());
        assertEquals(original.getDescription(), fromManager.get().getDescription());
        assertEquals(original.getStatus(), fromManager.get().getStatus());

    }

    @Test
    void shouldCalculateEndTime() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Task task = new Task(1, "Title", "Desc", TaskStatus.NEW, duration, startTime);

        assertEquals(startTime.plus(duration), task.getEndTime());
    }
}

