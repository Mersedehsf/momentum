package org.momentum.services;

import org.momentum.models.BaseEntity;
import org.momentum.repos.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseService<E extends BaseEntity,R extends BaseRepository<E>>  {

    @Autowired
    protected R repository;

    public abstract E create(String taskTitle);
}
