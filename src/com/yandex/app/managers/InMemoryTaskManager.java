package com.yandex.app.managers;

import com.yandex.app.model.Task;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Epic;
import com.yandex.app.model.TaskStatus;
import com.yandex.app.utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();


    // Методы для задач

    protected final Set<Task> prioritizedTasks = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null)
            return Integer.compare(o1.getId(), o2.getId());
        if (o1.getStartTime() == null) return 1;
        if (o2.getStartTime() == null) return -1;

        int cmp = o1.getStartTime().compareTo(o2.getStartTime());
        return (cmp != 0) ? cmp : Integer.compare(o1.getId(), o2.getId());
    });

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected boolean isOverlapping(Task t1, Task t2) {
        if (t1.getStartTime() == null || t1.getEndTime() == null ||
                t2.getStartTime() == null || t2.getEndTime() == null) {
            return false;
        }
        return !(t1.getEndTime().isBefore(t2.getStartTime()) || t2.getEndTime().isBefore(t1.getStartTime()));
    }

    protected boolean hasTimeIntersection(Task newTask) {
        return prioritizedTasks.stream()
                .filter(existing -> existing.getId() != newTask.getId())
                .anyMatch(existing -> isOverlapping(existing, newTask));
    }


    @Override
    public int createTask(Task task) {
        if (hasTimeIntersection(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой.");
        }
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task.getId();
    }

    @Override
    public List<Task> getTasks() {

        return new ArrayList<>(tasks.values());
    }


    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null && oldTask.getStartTime() != null) {
            prioritizedTasks.remove(oldTask);
        }

        if (hasTimeIntersection(task)) {
            if (oldTask != null && oldTask.getStartTime() != null) {
                prioritizedTasks.add(oldTask); // откат
            }
            throw new IllegalArgumentException("Обновление невозможно: задача пересекается с другой.");
        }

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }

        tasks.clear();
    }

    // Методы для подзадач
    @Override
    public int createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) return -1;

        if (hasTimeIntersection(subtask)) {
            throw new IllegalArgumentException("Ошибка: подзадача пересекается с другой задачей по времени.");
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        updateEpicStatus(epic);
        updateEpicTime(epic);

        return subtask.getId();
    }


    @Override
    public List<Subtask> getSubtasks() {

        return new ArrayList<>(subtasks.values());
    }


    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Subtask sub = subtasks.get(id);
        if (sub != null) {
            historyManager.add(sub);
            return Optional.of(sub);
        }
        return Optional.empty();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask == null) return;

        if (oldSubtask.getStartTime() != null) {
            prioritizedTasks.remove(oldSubtask);
        }

        if (hasTimeIntersection(subtask)) {
            // откат
            if (oldSubtask.getStartTime() != null) {
                prioritizedTasks.add(oldSubtask);
            }
            throw new IllegalArgumentException("Ошибка: подзадача пересекается с другой задачей по времени.");
        }

        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }


    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            historyManager.remove(id);
        }
    }


    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }


    // Методы для эпиков
    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public List<Epic> getEpics() {

        return new ArrayList<>(epics.values());
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return Optional.of(epic);
        }
        return Optional.empty();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) return;

        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }


    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null && subtask.getStartTime() != null) {
                    prioritizedTasks.remove(subtask);
                }
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }


    @Override
    public void deleteAllEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        subtasks.clear();
    }


    // Дополнительные методы
    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        List<Subtask> subtasksList = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        boolean allDone = subtasksList.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE);
        boolean allNew = subtasksList.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW);

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    private void updateEpicTime(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null || subtask.getStartTime() == null || subtask.getDuration() == null) {
                continue;
            }
            LocalDateTime subtaskStart = subtask.getStartTime();
            LocalDateTime subtaskEnd = subtask.getEndTime();

            if (earliestStart == null || subtaskStart.isBefore(earliestStart)) {
                earliestStart = subtaskStart;
            }
            if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                latestEnd = subtaskEnd;
            }

            totalDuration = totalDuration.plus(subtask.getDuration());
        }

        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
        epic.setDuration(totalDuration);
    }


}




