package managers;

import com.yandex.app.managers.HistoryManager;
import com.yandex.app.managers.InMemoryHistoryManager;
import com.yandex.app.model.Task;

import java.util.List;

import static com.yandex.app.model.TaskStatus.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class InMemoryHistoryManagerTest {
    private HistoryManager history;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        history = new InMemoryHistoryManager();
        task1 = new Task(1, "Task 1", "Description", NEW);
        task2 = new Task(2, "Task 2", "Description", IN_PROGRESS);
        task3 = new Task(3, "Task 3", "Description", DONE);
    }

    @Test
    void shouldAddTasksAndMaintainOrder() {
        history.add(task1);
        history.add(task2);
        history.add(task3);

        List<Task> historyList = history.getHistory();
        assertEquals(3, historyList.size());
        assertEquals(task1, historyList.get(0));
        assertEquals(task2, historyList.get(1));
        assertEquals(task3, historyList.get(2));
    }

    @Test
    void shouldRemoveDuplicates() {
        history.add(task1);
        history.add(task2);
        history.add(task1);

        List<Task> historyList = history.getHistory();
        assertEquals(2, historyList.size());
        assertEquals(task2, historyList.get(0));
        assertEquals(task1, historyList.get(1));
    }

    @Test
    void shouldRemoveTasksFromHistory() {
        history.add(task1);
        history.add(task2);
        history.remove(1);

        List<Task> historyList = history.getHistory();
        assertEquals(1, historyList.size());
        assertEquals(task2, historyList.getFirst());
    }
}
