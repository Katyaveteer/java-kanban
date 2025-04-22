package utils;

import com.yandex.app.managers.HistoryManager;
import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.Task;
import com.yandex.app.utils.Managers;

import org.junit.jupiter.api.Test;

import static com.yandex.app.model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    void getDefaultShouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);

        // Проверяем базовую функциональность
        int taskId = manager.createTask(new Task(1, "Title", "Description", NEW));
        assertNotNull(manager.getTaskById(taskId));
    }

    @Test
    void getDefaultHistoryShouldReturnWorkingHistoryManager() {
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history);

        history.add(new Task(1, "Title", "Description", NEW));
        assertEquals(1, history.getHistory().size());
    }


}