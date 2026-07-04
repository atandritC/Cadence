package com.cadence.project;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cadence.common.exception.ResourceNotFoundException;
import com.cadence.common.workflow.StatusWorkflow;
import com.cadence.project.dto.CreateProjectRequest;
import com.cadence.project.dto.ProjectResponse;
import com.cadence.project.dto.UpdateProjectStatusRequest;
import com.cadence.user.User;
import com.cadence.user.UserRepository;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @CacheEvict(value = "projects", key = "#projectId")
    @Transactional
    public ProjectResponse addMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        project.addMember(user);
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User manager = userRepository.findById(request.managerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.managerId()));

        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDueDate(request.dueDate());
        project.setManager(manager);

        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    @Cacheable(value = "projects", key = "#id")
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        return ProjectMapper.toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByManager(Long managerId) {
        return projectRepository.findByManagerId(managerId).stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "projects", key = "#id")
    @Transactional
    public ProjectResponse updateStatus(Long id, UpdateProjectStatusRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        ProjectStatus current = project.getStatus();
        ProjectStatus target = request.status();

        StatusWorkflow.validate(current, target, project.getStatusBeforeHold());

        if (target == ProjectStatus.ON_HOLD) {
            project.setStatusBeforeHold(current);
        } else if (current == ProjectStatus.ON_HOLD) {
            project.setStatusBeforeHold(null);
        }

        project.setStatus(target);
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

}
