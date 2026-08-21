package org.momentum.repos;

import org.momentum.models.task.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends BaseRepository<Category>{
    Category findByTitle(String title);
}
