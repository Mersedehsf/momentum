package org.momentum.services;

import org.momentum.dto.TaskDTO;
import org.momentum.models.task.Task;
import org.momentum.repos.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class TaskService extends BaseService<Task, TaskRepository>{

    @Override
    public Task create(String taskTitle) {
        Task task = new Task();
        task.setTitle(taskTitle);
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

    public void updateTask(Long id, Task updatedTask) {
        Task foundedTask = findById(id);
        foundedTask = updatedTask;
        repository.save(foundedTask);
    }
}
