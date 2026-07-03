package com.cadence.project;

import java.util.List;

import com.cadence.project.dto.CreateProjectRequest;
import com.cadence.project.dto.ProjectResponse;
import com.cadence.project.dto.UpdateProjectStatusRequest;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);

    ProjectResponse getProjectById(Long id);

    List<ProjectResponse> getProjectsByManager(Long managerId);

    ProjectResponse addMember(Long projectId, Long userId);

    public ProjectResponse updateStatus(Long id, UpdateProjectStatusRequest request);

}
