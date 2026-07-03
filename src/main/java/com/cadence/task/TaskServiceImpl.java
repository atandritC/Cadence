package com.cadence.task;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cadence.common.exception.BusinessRuleException;
import com.cadence.common.exception.ResourceNotFoundException;
import com.cadence.common.workflow.StatusWorkflow;
import com.cadence.project.Project;
import com.cadence.project.ProjectRepository;
import com.cadence.task.dto.CreateTaskRequest;
import com.cadence.task.dto.TaskResponse;
import com.cadence.task.dto.UpdateTaskStatusRequest;
import com.cadence.user.User;
import com.cadence.user.UserRepository;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.projectId()));

        // Business rule: a task cannot be due after its project's due date.
        if (request.dueDate() != null
                && project.getDueDate() != null
                && request.dueDate().isAfter(project.getDueDate())) {
            throw new BusinessRuleException(
                    "Task due date (" + request.dueDate() + ") cannot be after the project due date ("
                            + project.getDueDate() + ")");
        }

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStoryPoints(request.storyPoints());
        task.setDueDate(request.dueDate());
        task.setProject(project);
        task.setStatus(TaskStatus.TODO);

        if (request.assigneeId() != null) {
            User assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("User not found with id: " + request.assigneeId()));
            task.setAssignee(assignee);
        }

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return TaskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectIdWithDetails(projectId).stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(Long id, UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        TaskStatus current = task.getStatus();
        TaskStatus target = request.status();

        StatusWorkflow.validate(current, target, task.getStatusBeforeBlocked());

        if (target == TaskStatus.BLOCKED) {
            task.setStatusBeforeBlocked(current); // remember where to resume
        } else if (current == TaskStatus.BLOCKED) {
            task.setStatusBeforeBlocked(null); // leaving BLOCKED → clear memory
        }

        task.setStatus(target);
        return TaskMapper.toResponse(taskRepository.save(task));
    }

}