package managers;

import com.yandex.app.managers.FileBackedTaskManager;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskStatus;
import org.junit.Test;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileBackedTaskManagerTest {
    @Test
    public void saveAndLoadEmptyManager() throws IOException {
        File tempFile = File.createTempFile("task_manager", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        manager.save(); // Пустой менеджер сохраняется

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
        assertTrue(loaded.getSubtasks().isEmpty());
    }

    @Test
    public void saveMultipleTasksToFile() throws IOException {
        File tempFile = File.createTempFile("task_manager", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Epic epic = new Epic("Epic 1", "Epic description");
        Subtask subtask = new Subtask("Subtask 1", "Sub description", TaskStatus.NEW, epic.getId());

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        manager.save();

        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertTrue(lines.size() > 1); // Заголовок + строки с задачами
    }

    @Test
    public void loadMultipleTasksFromFile() throws IOException {
        File tempFile = File.createTempFile("task_manager", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Epic epic = new Epic("Epic 1", "Epic description", TaskStatus.NEW);

        int taskId = manager.createTask(task);
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Sub description", TaskStatus.NEW, epicId);
        manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loaded.getTasks().size());
        assertEquals(1, loaded.getEpics().size());
        assertEquals(1, loaded.getSubtasks().size());
    }


}
