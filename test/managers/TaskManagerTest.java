package managers;

import com.yandex.app.managers.TaskManager;
import com.yandex.app.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected Task task1;
    protected Epic epic1;
    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    public void setUp() {
        // Инициализация общих задач для тестов
        task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusDays(1));
        epic1 = new Epic("Epic 1", "Epic Description");
        subtask1 = new Subtask(1, "Subtask 1", "Subtask Desc 1", 0, TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusDays(2));
        subtask2 = new Subtask(2, "Subtask 2", "Subtask Desc 2", 0, TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.now().plusDays(3));
    }

    @Test
    public void testCreateAndGetTask() {
        int taskId = manager.createTask(task1);
        Optional<Task> retrieved = manager.getTaskById(taskId);
        assertTrue(retrieved.isPresent());
        assertEquals(task1.getTitle(), retrieved.get().getTitle());
        assertEquals(task1.getStatus(), retrieved.get().getStatus());
    }

    @Test
    public void testUpdateTask() {
        int id = manager.createTask(task1);
        Task updatedTask = new Task(id, "Updated Title", "Updated Desc", TaskStatus.DONE, Duration.ofMinutes(30), LocalDateTime.now().plusDays(1));
        manager.updateTask(updatedTask);
        Optional<Task> retrieved = manager.getTaskById(id);
        assertTrue(retrieved.isPresent());
        assertEquals("Updated Title", retrieved.get().getTitle());
        assertEquals(TaskStatus.DONE, retrieved.get().getStatus());
    }

    @Test
    public void testDeleteTask() {
        int id = manager.createTask(task1);
        manager.deleteTaskById(id);

        Optional<Task> opt = manager.getTaskById(id);
        assertTrue(opt.isEmpty(), "После удаления задача не должна возвращаться");
    }


    @Test
    public void testDeleteAllTasks() {
        manager.createTask(task1);
        manager.deleteAllTasks();
        List<Task> tasks = manager.getTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testCreateAndGetEpicWithSubtasks() {
        int epicId = manager.createEpic(epic1);
        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        int sub1Id = manager.createSubtask(subtask1);
        int sub2Id = manager.createSubtask(subtask2);

        List<Subtask> subtasks = manager.getSubtasksByEpicId(epicId);
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == sub1Id));
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == sub2Id));
    }

    @Test
    public void testEpicStatusAllNew() {
        int epicId = manager.createEpic(epic1);
        subtask1.setEpicId(epicId);
        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setEpicId(epicId);
        subtask2.setStatus(TaskStatus.NEW);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Optional<Epic> epic = manager.getEpicById(epicId);
        assertTrue(epic.isPresent());
        assertEquals(TaskStatus.NEW, epic.get().getStatus());
    }

    @Test
    public void testEpicStatusAllDone() {
        int epicId = manager.createEpic(epic1);
        subtask1.setEpicId(epicId);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setEpicId(epicId);
        subtask2.setStatus(TaskStatus.DONE);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Optional<Epic> epic = manager.getEpicById(epicId);
        assertTrue(epic.isPresent());
        assertEquals(TaskStatus.DONE, epic.get().getStatus());
    }

    @Test
    public void testEpicStatusNewAndDone() {
        int epicId = manager.createEpic(epic1);
        subtask1.setEpicId(epicId);
        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setEpicId(epicId);
        subtask2.setStatus(TaskStatus.DONE);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Optional<Epic> epic = manager.getEpicById(epicId);
        assertTrue(epic.isPresent());
        assertEquals(TaskStatus.IN_PROGRESS, epic.get().getStatus());
    }

    @Test
    public void testEpicStatusInProgress() {
        int epicId = manager.createEpic(epic1);
        subtask1.setEpicId(epicId);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setEpicId(epicId);
        subtask2.setStatus(TaskStatus.NEW);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Optional<Epic> epic = manager.getEpicById(epicId);
        assertTrue(epic.isPresent());
        assertEquals(TaskStatus.IN_PROGRESS, epic.get().getStatus());
    }

    @Test
    public void testGetHistory() {
        int id1 = manager.createTask(task1);
        int epicId = manager.createEpic(epic1);
        subtask1.setEpicId(epicId);
        int subId = manager.createSubtask(subtask1);

        manager.getTaskById(id1);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subId);

        List<Task> history = manager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    public void testGetPrioritizedTasks_NoOverlap() {
        Task taskA = new Task("A", "desc", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task taskB = new Task("B", "desc", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        manager.createTask(taskA);
        manager.createTask(taskB);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertTrue(prioritized.get(0).getStartTime().isBefore(prioritized.get(1).getStartTime()));
    }

    @Test
    public void testIntervalOverlap() {
        Task taskA = new Task("A", "desc", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.now().plusHours(1));
        Task taskB = new Task("B", "desc", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1).plusMinutes(30));
        manager.createTask(taskA);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> manager.createTask(taskB));
        String expectedMessage = "Задача пересекается по времени с другой.";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

}
