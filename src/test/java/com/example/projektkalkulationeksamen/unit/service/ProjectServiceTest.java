package com.example.projektkalkulationeksamen.unit.service;

import com.example.projektkalkulationeksamen.dto.MilestoneDTO;
import com.example.projektkalkulationeksamen.exceptions.database.DatabaseException;
import com.example.projektkalkulationeksamen.exceptions.database.DeletionException;
import com.example.projektkalkulationeksamen.exceptions.notfound.ProjectNotFoundException;
import com.example.projektkalkulationeksamen.exceptions.project.ProjectCreationException;
import com.example.projektkalkulationeksamen.exceptions.project.ProjectUpdateException;
import com.example.projektkalkulationeksamen.model.Project;
import com.example.projektkalkulationeksamen.model.Status;
import com.example.projektkalkulationeksamen.repository.ProjectRepository;
import com.example.projektkalkulationeksamen.service.MilestoneService;
import com.example.projektkalkulationeksamen.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private Project testProject;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MilestoneService milestoneService;

    @InjectMocks
    ProjectService projectService;


    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1);
        testProject.setProjectName("Testprojekt");
        testProject.setDescription("En beskrivelse");
        testProject.setProjectManagerId(1);
        testProject.setStatus(Status.IN_PROGRESS);

        LocalDateTime now = LocalDateTime.of(2025, 5, 20, 10, 0);
        testProject.setCreatedAt(now);
        testProject.setStartDate(now.plusDays(1));
        testProject.setDeadline(now.plusDays(30));
    }


    @Test
    void addProject_shouldSaveProject_whenValidInput() {
        // arrange
        Project projectToAdd = testProject;
        List<Project> projectList = new ArrayList<>();

        // Mock behavior
        Mockito.when(projectRepository.addProject(projectToAdd)).thenAnswer(invocation -> {
            projectList.add(projectToAdd);
            return projectToAdd;
        });

        // Act
        projectService.addProject(projectToAdd);

        // Assert
        assertEquals(projectToAdd, projectList.getFirst());

        // Verify
        Mockito.verify(projectRepository, Mockito.times(1)).addProject(testProject);

    }

    @Test
    void addProject_shouldThrowException_whenRepositoryFails() {

        // arrange
        Project invalidProject = testProject;

        // mock behavior
        Mockito.when(projectRepository.addProject(invalidProject)).thenThrow(new DatabaseException("Failed to create project in Database"));

        // act and assert: expect exception
        Exception exception = assertThrows(ProjectCreationException.class, () -> projectService.addProject(invalidProject));

        // assert
        assertEquals("Failed to create project with project name: " + invalidProject.getProjectName(), exception.getMessage());

        // Verify
        Mockito.verify(projectRepository).addProject(invalidProject);
    }

    @Test
    void updateProject_shouldUpdateProject_whenValidInput() {
        // Arrange
        Project projectToUpdate = testProject;

        // Mock behavior
        Mockito.when(projectRepository.updateProject(projectToUpdate)).thenAnswer(invocation -> {
            return true; // the update method returns a boolean.
        });

        Mockito.when(projectRepository.findProjectById(1)).thenReturn(Optional.of(testProject));

        // Act
        projectService.updateProject(projectToUpdate);


        // Verify
        Mockito.verify(projectRepository).updateProject(projectToUpdate);
    }

    @Test
    void updateProject_shouldThrowException_whenRepositoryFails() {
        // Arrange
        Project project = testProject;

        // Mock behavior
        Mockito.when(projectRepository.updateProject(project)).thenThrow(new DatabaseException("Failed to update project in Database"));
        Mockito.when(projectRepository.findProjectById(1)).thenReturn(Optional.of(testProject));

        // Act & Assert: expect exception
        Exception exception = assertThrows(ProjectUpdateException.class, () ->
                projectService.updateProject(testProject)
        );

        // Assert
        assertEquals("Database error while updating project with ID: " + project.getId(), exception.getMessage());

        // Verify
        Mockito.verify(projectRepository).updateProject(project);
    }


    @Test
    void deleteProject_shouldDeleteProject_whenExists() {
        // Arrange
        int projectId = testProject.getId();
        Mockito.when(projectRepository.deleteProject(projectId)).thenReturn(true);

        // Act
        projectService.deleteProject(projectId);

        // Verify
        Mockito.verify(projectRepository).deleteProject(projectId);
    }

    @Test
    void deleteProject_shouldThrowException_whenProjectNotFound() {
        // Arrange
        int projectId = testProject.getId();
        Mockito.when(projectRepository.deleteProject(projectId)).thenReturn(false);

        // Act & Assert: expect exception
        Exception exception = assertThrows(ProjectNotFoundException.class, () ->
                projectService.deleteProject(projectId)
        );

        // Assert
        assertEquals("Failed to delete project with ID: " + projectId, exception.getMessage());

        // Verify
        Mockito.verify(projectRepository).deleteProject(projectId);
    }

    @Test
    void deleteProject_shouldThrowException_whenRepositoryFails() {
        // Arrange
        int projectId = testProject.getId();
        Mockito.when(projectRepository.deleteProject(projectId))
                .thenThrow(new DatabaseException("Failed to delete project"));

        // Act & Assert: expect exception
        Exception exception = assertThrows(DeletionException.class, () ->
                projectService.deleteProject(projectId)
        );

        // Assert
        assertEquals("Failed to delete project with ID: " + projectId, exception.getMessage());

        // Verify
        Mockito.verify(projectRepository).deleteProject(projectId);
    }

    @Test
    void getProjectProgress_shouldReturnZero_whenNoMilestones() {
        // Arrange
        int projectId = testProject.getId();

        // mockito behavior
        Mockito.when(milestoneService.getMilestonesByProjectIdWithDetails(projectId)).thenReturn(List.of());

        // Act
        int progress = projectService.getProjectProgress(projectId);

        // Assert
        assertEquals(0, progress);

        // Verify
        Mockito.verify(milestoneService).getMilestonesByProjectIdWithDetails(projectId);
    }

    @Test
    void getProjectProgress_shouldReturnCorrectPercentage_whenSomeMilestonesCompleted() {
        // Arrange
        int projectId = testProject.getId();
        MilestoneDTO completed = Mockito.mock(MilestoneDTO.class);
        MilestoneDTO notCompleted = Mockito.mock(MilestoneDTO.class);

        List<MilestoneDTO> milestones = List.of(completed, notCompleted, notCompleted); // 1/3 completed


        // mockito behavior
        Mockito.when(completed.getMilestoneStatus()).thenReturn(Status.COMPLETED);
        Mockito.when(notCompleted.getMilestoneStatus()).thenReturn(Status.IN_PROGRESS);
        Mockito.when(milestoneService.getMilestonesByProjectIdWithDetails(projectId)).thenReturn(milestones);

        // Act
        int progress = projectService.getProjectProgress(projectId);

        // Assert
        assertEquals(33, progress); // round from 33.33 to 33

        // Verify
        Mockito.verify(milestoneService).getMilestonesByProjectIdWithDetails(projectId);
    }

    @Test
    void getProjectProgress_shouldReturn100_whenAllMilestonesCompleted() {
        // Arrange
        int projectId = testProject.getId();
        MilestoneDTO completed = Mockito.mock(MilestoneDTO.class);
        List<MilestoneDTO> milestones = List.of(completed, completed, completed); // 100% completed

        // mockito behavior
        Mockito.when(completed.getMilestoneStatus()).thenReturn(Status.COMPLETED);
        Mockito.when(milestoneService.getMilestonesByProjectIdWithDetails(projectId)).thenReturn(milestones);

        // Act
        int progress = projectService.getProjectProgress(projectId);

        // Assert
        assertEquals(100, progress);

        // Verify
        Mockito.verify(milestoneService).getMilestonesByProjectIdWithDetails(projectId);
    }

}
