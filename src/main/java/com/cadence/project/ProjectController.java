package com.cadence.project;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cadence.project.dto.CreateProjectRequest;
import com.cadence.project.dto.ProjectResponse;
import com.cadence.project.dto.UpdateProjectStatusRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cadence/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @GetMapping
    public List<ProjectResponse> getByManager(@RequestParam Long managerId) {
        return projectService.getProjectsByManager(managerId);
    }

    @PostMapping("/{id}/members/{userId}")
    public ProjectResponse addMember(@PathVariable Long id, @PathVariable Long userId) {
        return projectService.addMember(id, userId);
    }

    @PatchMapping("/{id}/status")
    public ProjectResponse updateStatus(@PathVariable Long id,
            @Valid @RequestBody UpdateProjectStatusRequest request) {
        return projectService.updateStatus(id, request);
    }

}