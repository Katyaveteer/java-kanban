package test.com.yandex.app.model;

import com.yandex.app.managers.TaskManager;

import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.Test;

import static com.yandex.app.model.TaskStatus.IN_PROGRESS;
import static com.yandex.app.model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {


    private final TaskManager manager = Managers.getDefault();


    @Test
    void epicWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic(1, "Title", "Description", NEW);
        Epic epic2 = new Epic(1, "Different Title", "Different Desc", IN_PROGRESS);
        assertEquals(epic1, epic2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        Epic epic = new Epic(1, "Title", "Description", NEW);
        int epicId = manager.createEpic(epic);

        Subtask invalidSubtask = new Subtask(1, "Title", "Description", NEW, epicId);
        invalidSubtask.setEpicId(epicId); // Попытка сделать эпиком самого себя

    }
}