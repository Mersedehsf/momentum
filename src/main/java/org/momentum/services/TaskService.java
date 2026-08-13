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
}
