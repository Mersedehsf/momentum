package org.momentum.services;

import org.momentum.dto.TaskDTO;
import org.momentum.models.task.Category;
import org.momentum.models.task.Task;
import org.momentum.repos.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class TaskService extends BaseService<Task, TaskRepository>{//todo handle deleting as well

    @Override
    public Task create(String taskTitle) {
        Task task = new Task();
        task.setTitle(taskTitle);
        return repository.save(task);
    }

    public Task createTask(String title, Integer estimatedMinutes, Category category, String comment) {
        Task task = new Task(title, estimatedMinutes, category, comment, 0);
        return repository.save(task);
    }

    public Task completeTask(Long taskId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new RuntimeException());//todo
        task.setCompleted(1);
        task.setCompletionTime(Instant.now());
        return repository.save(task);
    }

    public List<TaskDTO> findTodaysTasks(){
        ZoneId zone = ZoneId.of("Asia/Tehran");

        LocalDate today = LocalDate.now(zone);

        Instant startOfDay = today
                .atStartOfDay(zone)
                .toInstant();

        Instant startOfTomorrow = today
                .plusDays(1)
                .atStartOfDay(zone)
                .toInstant();
        return repository.getTodaysTasks(startOfDay,startOfTomorrow);
    }

    public Task findById(Long taskId){
        return repository.findById(taskId).orElse(null);
    }

    public void softDelete(Long taskId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new RuntimeException());
        task.setDeleted(1);
        repository.save(task);
    }

    public void updateTask(Long id, Task updatedTask) {
        Task existingTask = findById(id);
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setEstimatedMinutes(updatedTask.getEstimatedMinutes());
        existingTask.setCategory(updatedTask.getCategory());
        existingTask.setComment(updatedTask.getComment());
        existingTask.setCompleted(updatedTask.getCompleted());
        repository.save(existingTask);
    }
}
