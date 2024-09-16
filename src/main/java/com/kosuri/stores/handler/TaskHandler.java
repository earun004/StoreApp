package com.kosuri.stores.handler;


import com.kosuri.stores.dao.TaskEntity;
import com.kosuri.stores.dao.TaskRepository;
import com.kosuri.stores.dao.TaskRoleEntity;
import com.kosuri.stores.dao.TaskRoleRepository;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.request.AddTaskRequest;
import com.kosuri.stores.model.request.AddUserRequest;
import com.kosuri.stores.model.request.GetTasksForRoleRequest;
import com.kosuri.stores.model.request.MapTaskForRoleRequest;
import com.kosuri.stores.model.response.AddTaskResponse;
import com.kosuri.stores.model.response.GetAllTasksResponse;
import com.kosuri.stores.model.response.GetTasksForRoleResponse;
import com.kosuri.stores.model.role.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.fasterxml.jackson.databind.type.LogicalType.Array;


@Service
public class TaskHandler {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskRoleRepository taskRoleRepository;


    public GetAllTasksResponse getAllTasks(){
        GetAllTasksResponse response = new GetAllTasksResponse();
        List<TaskEntity> taskList = new ArrayList<TaskEntity>();
        taskRepository.findAll().forEach(task -> taskList.add(task));
        response.setTaskList(taskList);
        return response;
    }

    public AddTaskResponse addTask(AddTaskRequest request) throws Exception {
        AddTaskResponse response = new AddTaskResponse();
        if(taskRepository.findById(request.getTaskId()).isPresent() || taskRepository.findByTaskName(request.getTaskName()).isPresent()){
            throw new APIException("Task with id/name already exists");
        }
        TaskEntity task = new TaskEntity();
        task.setTaskId(request.getTaskId());
        task.setTaskName(request.getTaskName());

        taskRepository.save(task);
        response.setTaskId(task.getTaskId());
        response.setMsg("Task added successfully");
        return response;
    }

    public void mapTaskRoleEntityFromRequest(MapTaskForRoleRequest request) throws Exception {
        List<TaskRoleEntity> existingTaskForRole = new ArrayList<>();
        existingTaskForRole = taskRoleRepository.findByRoleId(request.getRoleId());
        for(TaskRoleEntity temp: existingTaskForRole ){
            boolean check = Collections.singletonList(request.getTaskIds()).contains(temp.getTaskId());
            if(!check){
                taskRoleRepository.delete(temp);
            }
        }
        for(Integer taskId: request.getTaskIds()) {
            TaskRoleEntity tempTaskRole = new TaskRoleEntity();
            tempTaskRole.setTaskId(taskId);
            Optional<TaskEntity> taskEntityOptional = taskRepository.findById(taskId);

            if (taskEntityOptional.isEmpty()) {
                throw new APIException(String.format("Task with id %s not found", taskId));
            }

            tempTaskRole.setTaskName(taskEntityOptional.get().getTaskName());
            tempTaskRole.setRoleId(request.getRoleId());
            tempTaskRole.setUpdatedBy(request.getUpdatedBy());
            LocalDate localDate = LocalDate.now();
            tempTaskRole.setUpdatedDate(localDate.toString());
            try {
                taskRoleRepository.save(tempTaskRole);
            } catch(Exception e){
                System.out.println(e.getCause());
            }
        }

    }


    public GetTasksForRoleResponse fetchAllTaskOfRole(GetTasksForRoleRequest request) {
        List<TaskRoleEntity> savedTaskForRole = taskRoleRepository.findByRoleId(request.getRoleId());
        List<Task> taskList = new ArrayList<>();

        for(TaskRoleEntity taskRoleEntity: savedTaskForRole) {
            Task task = new Task();
            task.setTaskId(taskRoleEntity.getTaskId());
            task.setTaskName(taskRoleEntity.getTaskName());
            taskList.add(task);
        }
        GetTasksForRoleResponse getTasksForRoleResponse = new GetTasksForRoleResponse();
        getTasksForRoleResponse.setTaskList(taskList);
        return getTasksForRoleResponse;

    }



}

