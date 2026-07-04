package com.cadence.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cadence.common.exception.ResourceNotFoundException;
import com.cadence.project.dto.CreateProjectRequest;
import com.cadence.project.dto.ProjectResponse;
import com.cadence.user.User;
import com.cadence.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void createProject_whenManagerDoesNotExist_throwsNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        var request = new CreateProjectRequest("Website", "desc", null, 99L);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> projectService.createProject(request));

        // Verification
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_success_savesAndReturnsResponse() {
        // Arrange
        User manager = new User();
        manager.setId(1L); // setId comes from BaseEntity (Lombok)
        manager.setEmail("dummy@x.com");
        manager.setFullName("Dummy");
        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));

        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));
        var request = new CreateProjectRequest("Website", "desc", null, 1L);

        // Act
        ProjectResponse response = projectService.createProject(request);

        // Assert
        assertEquals("Website", response.name());
        assertEquals(1L, response.managerId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

}
