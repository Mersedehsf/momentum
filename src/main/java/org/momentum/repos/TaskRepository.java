package org.momentum.repos;


import org.momentum.dto.TaskDTO;
import org.momentum.models.task.Task;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskRepository extends BaseRepository<Task>{

    @Query("SELECT new org.momentum.dto.TaskDTO(t.id,t.title,c.title,t.comment,t.completed) FROM Task t Left Join t.category c where t.deleted = 0 and t.creationTime >= :startOfDay and t.creationTime < :startOfTomorrow ")
    List<TaskDTO> getTodaysTasks(@Param("startOfDay") Instant startOfDay, @Param("startOfTomorrow") Instant startOfTomorrow);

    @Query("SELECT t FROM Task t WHERE t.deleted = 0 AND t.creationTime >= :from AND t.creationTime < :to")
    List<Task> findCreatedBetween(@Param("from") Instant from, @Param("to") Instant to);

}
