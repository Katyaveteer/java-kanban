package model;

import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.yandex.app.model.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private final TaskManager manager = Managers.getDefault();

    @Test
    void epicWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Title", "Description");
        Epic epic2 = new Epic("Different Title", "Different Desc");
        epic2.setId(epic1.getId()); // Устанавливаем один и тот же ID
        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        Epic epic = new Epic("Title", "Description");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask(1, "Sub", "Desc", epicId, NEW, Duration.ofMinutes(30), LocalDateTime.now());
        subtask.setId(epicId); // Попытка задать id сабтаска = id эпика

        int subtaskId = manager.createSubtask(subtask);

        List<Subtask> subtasks = manager.getSubtasksByEpicId(epicId);
        assertFalse(subtasks.stream().anyMatch(s -> s.getId() == epicId), "Эпик не должен быть своей же подзадачей");
        assertNotEquals(epicId, subtaskId, "Id подзадачи не должен совпадать с id эпика");
    }

    @Test
    void shouldHandleTimeWithoutSubtasks() {
        Epic epic = new Epic("Title", "Desc");
        assertNull(epic.getStartTime(), "Без подзадач начало должно быть null");
        assertNull(epic.getEndTime(), "Без подзадач конец должен быть null");
        assertEquals(Duration.ZERO, epic.getDuration(), "Без подзадач продолжительность = 0");
    }

    @Test
    void statusShouldBeNewIfAllSubtasksNew() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask("Sub1", "", epicId, NEW));
        manager.createSubtask(new Subtask("Sub2", "", epicId, NEW));

        Optional<Epic> updatedEpic = manager.getEpicById(epic.getId());
        assertTrue(updatedEpic.isPresent());
        assertEquals(NEW, updatedEpic.get().getStatus());

    }

    @Test
    void statusShouldBeDoneIfAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask("Sub1", "", epicId, DONE));
        manager.createSubtask(new Subtask("Sub2", "", epicId, DONE));

        Optional<Epic> updatedEpic = manager.getEpicById(epic.getId());
        assertTrue(updatedEpic.isPresent());
        assertEquals(DONE, updatedEpic.get().getStatus());

    }

    @Test
    void statusShouldBeInProgressIfMixed() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask("Sub1", "", epicId, NEW));
        manager.createSubtask(new Subtask("Sub2", "", epicId, DONE));

        Optional<Epic> updatedEpic = manager.getEpicById(epic.getId());
        assertTrue(updatedEpic.isPresent());
        assertEquals(IN_PROGRESS, updatedEpic.get().getStatus());

    }

    @Test
    void statusShouldBeInProgressIfAnyInProgress() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask("Sub1", "", epicId, NEW));
        manager.createSubtask(new Subtask("Sub2", "", epicId, IN_PROGRESS));

        Optional<Epic> updatedEpic = manager.getEpicById(epic.getId());
        assertTrue(updatedEpic.isPresent());
        assertEquals(IN_PROGRESS, updatedEpic.get().getStatus());
    }
}
