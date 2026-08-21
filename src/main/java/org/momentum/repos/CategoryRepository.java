package org.momentum.repos;

import org.momentum.dto.CategoryDTO;
import org.momentum.models.task.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends BaseRepository<Category>{
    Category findByTitle(String title);

    @Query("SELECT new org.momentum.dto.CategoryDTO(c.id,c.title,c.estimatedMinutes) FROM Category c where c.deleted = 0")
    List<CategoryDTO> findAllCategories();
}
