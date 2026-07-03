package com.cadence.task;

import java.util.List;

import com.cadence.task.dto.CreateTaskRequest;
import com.cadence.task.dto.TaskResponse;
import com.cadence.task.dto.UpdateTaskStatusRequest;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse getTaskById(Long id);

    List<TaskResponse> getTasksByProject(Long projectId);

    TaskResponse updateStatus(Long id, UpdateTaskStatusRequest request);

}