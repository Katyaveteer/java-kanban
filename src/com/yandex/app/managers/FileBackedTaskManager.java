package com.yandex.app.managers;

import com.yandex.app.server.NotFoundException;
import com.yandex.app.model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {

        this.file = file;
    }


    @Override
    public int createTask(Task task) {
        if (hasTimeIntersection(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой.");
        }
        int id = super.createTask(task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
        save();
        return id;
    }


    @Override
    public Task getTaskById(int id) throws NotFoundException {
        Task opt = super.getTaskById(id);
        save();
        return opt;
    }


    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null && oldTask.getStartTime() != null) {
            prioritizedTasks.remove(oldTask);
        }

        if (hasTimeIntersection(task)) {
            if (oldTask != null && oldTask.getStartTime() != null) {
                prioritizedTasks.add(oldTask); // возвращаем, если новая некорректна
            }
            throw new IllegalArgumentException("Ошибка: задача пересекается с другой по времени.");
        }

        super.updateTask(task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        save();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (hasTimeIntersection(subtask)) {
            throw new IllegalArgumentException("Ошибка: подзадача пересекается с другой по времени.");
        }

        int id = super.createSubtask(subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        save();
        return id;
    }

    @Override
    public Subtask getSubtaskById(int id) throws NotFoundException {
        Subtask opt = super.getSubtaskById(id);
        save();
        return opt;
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask != null && oldSubtask.getStartTime() != null) {
            prioritizedTasks.remove(oldSubtask);
        }

        if (hasTimeIntersection(subtask)) {
            if (oldSubtask != null && oldSubtask.getStartTime() != null) {
                prioritizedTasks.add(oldSubtask);
            }
            throw new IllegalArgumentException("Ошибка: подзадача пересекается с другой по времени.");
        }

        super.updateSubtask(subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        save();
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public Epic getEpicById(int id) throws NotFoundException {
        Epic opt = super.getEpicById(id);
        save();
        return opt;
    }


    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null && task.getStartTime() != null) prioritizedTasks.remove(task);
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }


    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null && subtask.getStartTime() != null) prioritizedTasks.remove(subtask);
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }


    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }


    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");

            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

            writer.newLine();

            List<Task> history = historyManager.getHistory();
            StringBuilder historyLine = new StringBuilder();

            for (int i = 0; i < history.size(); i++) {
                historyLine.append(history.get(i).getId());
                if (i < history.size() - 1) {
                    historyLine.append(",");
                }
            }
            if (!historyLine.isEmpty()) {
                writer.write(historyLine.toString());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + file.getName(), e);
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");


        if (task.getType() == TaskType.SUBTASK) {
            sb.append(((Subtask) task).getEpicId());
        }
        sb.append(",");

        // duration
        Duration duration = task.getDuration();
        if (duration != null) {
            sb.append(duration.toMinutes());
        }
        sb.append(",");

        // startTime
        LocalDateTime startTime = task.getStartTime();
        if (startTime != null) {
            sb.append(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        return sb.toString();
    }


    private Task fromString(String line) {
        String[] parts = line.split(",", -1); //

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        String epicIdStr = parts.length > 5 ? parts[5] : "";
        String durationStr = parts.length > 6 ? parts[6] : "";
        String startTimeStr = parts.length > 7 ? parts[7] : "";

        Duration duration = null;
        if (!durationStr.isBlank()) {
            try {
                duration = Duration.ofMinutes(Long.parseLong(durationStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректная продолжительность: " + durationStr, e);
            }
        }

        LocalDateTime startTime = null;
        if (!startTimeStr.isBlank()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                startTime = LocalDateTime.parse(startTimeStr, formatter);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Некорректное время начала: " + startTimeStr, e);
            }
        }

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status, duration, startTime);
            case "EPIC":
                return new Epic(id, name, description, status);
            case "SUBTASK":
                int epicId = Integer.parseInt(epicIdStr);
                return new Subtask(id, name, description, epicId, status, duration, startTime);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

    }


    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;


            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = manager.fromString(line);
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(task.getId(), subtask);

                        Epic epic = manager.epics.get(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());

                        }
                        break;
                }
                if (task.getType() != TaskType.EPIC && task.getStartTime() != null) {
                    manager.prioritizedTasks.add(task);
                }

                if (task.getId() >= manager.nextId) {
                    manager.nextId = task.getId() + 1;
                }

            }

            String historyLine = reader.readLine();

            if (historyLine != null && !historyLine.isEmpty()) {
                String[] idHistory = historyLine.split(",");
                for (String idStr : idHistory) {
                    int id = Integer.parseInt(idStr);
                    if (manager.tasks.containsKey(id)) {
                        manager.historyManager.add(manager.tasks.get(id));
                    } else if (manager.epics.containsKey(id)) {
                        manager.historyManager.add(manager.epics.get(id));
                    } else if (manager.subtasks.containsKey(id)) {
                        manager.historyManager.add(manager.subtasks.get(id));

                    }
                }
            }
            for (Epic epic : manager.epics.values()) {
                manager.updateEpic(epic);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла: " + file.getName(), e);
        }

        return manager;
    }


}
