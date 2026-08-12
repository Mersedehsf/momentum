package org.momentum.models;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity {

    @CreatedDate
    protected Date creationTime ;

    @LastModifiedDate
    protected Date updateAt;

    protected Integer deleted = 0;


}
