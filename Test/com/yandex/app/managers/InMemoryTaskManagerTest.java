package com.yandex.app.managers;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager manager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);

        manager.createSubtask(new Subtask("Subtask 1", "Desc", epicId));
        manager.createSubtask(new Subtask("Subtask 2", "Desc", epicId));
    }

    @Test
    void shouldRemoveSubtasksFromEpicWhenDeleted() {
        List<Subtask> subtasks = manager.getSubtasksByEpicId(epic.getId());
        int subtaskId = subtasks.getFirst().getId();

        manager.deleteSubtaskById(subtaskId);

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertFalse(updatedEpic.getSubtaskIds().contains(subtaskId));
    }

    @Test
    void shouldRemoveEpicAndSubtasksFromHistory() {
        int subtaskId = manager.getSubtasksByEpicId(epic.getId()).getFirst().getId();

        // Добавляем в историю
        manager.getTaskById(epic.getId());
        manager.getSubtaskById(subtaskId);

        // Удаляем эпик
        manager.deleteEpicById(epic.getId());

        assertTrue(manager.getHistory().isEmpty());
    }
}