package managers;

import com.yandex.app.managers.HistoryManager;
import com.yandex.app.managers.InMemoryHistoryManager;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager history;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        history = new InMemoryHistoryManager();
        task1 = new Task(1, "Task 1", "Description", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        task2 = new Task(2, "Task 2", "Description", TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.now().plusHours(2));
    }

    @Test
    void shouldPreserveTaskDataInHistory() {
        history.add(task1);
        Task fromHistory = history.getHistory().getFirst();

        assertEquals(task1.getTitle(), fromHistory.getTitle());
        assertEquals(task1.getDuration(), fromHistory.getDuration());
    }


    @Test
    void shouldHandleEmptyHistory() {
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveFromBeginning() {
        history.add(task1);
        history.add(task2);
        history.remove(task1.getId());

        assertEquals(List.of(task2), history.getHistory());
    }

    @Test
    void shouldRemoveFromMiddle() {
        Task task3 = new Task("Task 3", "Desc", TaskStatus.DONE);
        history.add(task1);
        history.add(task2);
        history.add(task3);
        history.remove(task2.getId());

        assertEquals(List.of(task1, task3), history.getHistory());
    }

    @Test
    void shouldRemoveFromEnd() {
        history.add(task1);
        history.add(task2);
        history.remove(task2.getId());

        assertEquals(List.of(task1), history.getHistory());
    }

    @Test
    void shouldNotDuplicateTasksInHistory() {
        history.add(task1);
        history.add(task2);
        history.add(task1);

        List<Task> historyList = history.getHistory();


        assertEquals(2, historyList.size());


        assertEquals(task2, historyList.get(0));
        assertEquals(task1, historyList.get(1));
    }

    @Test
    void shouldNotAddNullToHistory() {
        assertDoesNotThrow(() -> history.add(null), "Добавление null не должно приводить к ошибке");
        assertTrue(history.getHistory().isEmpty(), "История должна оставаться пустой");
    }


}