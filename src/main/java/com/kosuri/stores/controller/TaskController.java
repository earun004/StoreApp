package com.kosuri.stores.controller;

import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.RepositoryHandler;
import com.kosuri.stores.handler.TaskHandler;
import com.kosuri.stores.model.request.AddTaskRequest;
import com.kosuri.stores.model.request.MapTaskForRoleRequest;
import com.kosuri.stores.model.response.AddTaskResponse;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetAllTasksResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private RepositoryHandler repositoryHandler;
    @Autowired
    private TaskHandler taskHandler;


    @GetMapping("/getAll")
    public ResponseEntity<GetAllTasksResponse> getAllTasks() {
        GetAllTasksResponse response = new GetAllTasksResponse();
        try {
            response = taskHandler.getAllTasks();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<AddTaskResponse> addTask(@RequestBody AddTaskRequest request){
        AddTaskResponse response = new AddTaskResponse();
        try {
            response = taskHandler.addTask(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (APIException e){
            response.setMsg(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/map")
    public ResponseEntity<GenericResponse> mapTaskRoleEntityFromRequest(@RequestBody MapTaskForRoleRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            taskHandler.mapTaskRoleEntityFromRequest(request);
            response.setResponseMessage("Mapped successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

