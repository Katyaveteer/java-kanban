package com.yandex.app.managers;

import com.yandex.app.model.Task;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.Test;

import static com.yandex.app.model.TaskStatus.DONE;
import static com.yandex.app.model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private final HistoryManager history = Managers.getDefaultHistory();

    @Test
    void shouldPreserveTaskDataInHistory() {
        Task original = new Task(1, "Original", "Desc", NEW);
        history.add(original);


        original.setTitle("Modified");
        original.setStatus(DONE);

        Task fromHistory = history.getHistory().getFirst();


        assertEquals("Original", fromHistory.getTitle());
        assertEquals(NEW, fromHistory.getStatus());
    }

}