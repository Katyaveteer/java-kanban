package managers;

import com.yandex.app.managers.HistoryManager;
import com.yandex.app.model.Task;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static com.yandex.app.model.TaskStatus.DONE;
import static com.yandex.app.model.TaskStatus.NEW;



class HistoryManagerTest {

    private final HistoryManager history = Managers.getDefaultHistory();

    @Test
    void shouldPreserveTaskDataInHistory() {
        Task original = new Task(1, "Original", "Desc", NEW);
        history.add(original);


        original.setTitle("Modified");
        original.setStatus(DONE);

        Task fromHistory = history.getHistory().getFirst();


        Assertions.assertEquals("Original", fromHistory.getTitle());
        Assertions.assertEquals(NEW, fromHistory.getStatus());
    }

}