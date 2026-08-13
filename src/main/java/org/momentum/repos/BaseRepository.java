package org.momentum.repos;

import org.momentum.models.BaseEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseRepository<E extends BaseEntity> extends CrudRepository<E,Long> {


}
