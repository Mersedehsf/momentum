package org.momentum.services;

import org.momentum.models.task.Category;
import org.momentum.repos.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends BaseService<Category, CategoryRepository>{

    @Override
    public Category create(String taskTitle) {
        return null;
    }

    public Category findByTitle(String title){
        return repository.findByTitle(title);
    }
}
