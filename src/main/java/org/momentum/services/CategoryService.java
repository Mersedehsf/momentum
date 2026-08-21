package org.momentum.services;

import org.momentum.dto.CategoryDTO;
import org.momentum.models.task.Category;
import org.momentum.repos.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends BaseService<Category, CategoryRepository> {// todo handle globalException

    @Override
    public Category create(String categoryTitle) {
        Category category = new Category();
        category.setTitle(categoryTitle);
        return repository.save(category);
    }

    public Category findByTitle(String title) {
        return repository.findByTitle(title);
    }

    public List<CategoryDTO> findAll() {
        return repository.findAllCategories();
    }

    public Category findById(Long categoryId) {
        return repository.findById(categoryId).orElse(null);

    }

    public void updateCategory(Long id, Category newCategory) {
        Category foundedCategory = findById(id);
        foundedCategory = newCategory;
        repository.save(foundedCategory);
    }
}
