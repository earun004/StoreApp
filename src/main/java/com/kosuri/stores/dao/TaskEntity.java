package com.kosuri.stores.dao;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "role_management")
public class TaskEntity {

    @Id
    @Column(name="Task_ID")
    private Integer taskId;

    @Column(name="Task_Name",nullable = true, length = 45)
    private String taskName;


}
