package com.example.projektkalkulationeksamen.integration;
import com.example.projektkalkulationeksamen.model.Project;
import com.example.projektkalkulationeksamen.model.Status;
import com.example.projektkalkulationeksamen.repository.ProjectRepository;
import com.example.projektkalkulationeksamen.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:h2init.sql"}
)
public class ProjectFlowIntegrationTest {


    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;


    @Test
    void shouldCreateProjectSuccessfully() throws SQLException {

        // arrange
        String projectName = "testProject";
        String description = "testDescription";
        int projectManagerId = 2;
        LocalDateTime deadline = LocalDateTime.of(2025, 9, 5, 2, 0);
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 5, 2, 0);
        Project newProject = new Project();
        newProject.setProjectName(projectName);
        newProject.setDescription(description);
        newProject.setProjectManagerId(projectManagerId);
        newProject.setDeadline(deadline);
        newProject.setStartDate(startDate);

        // act
        projectService.addProject(newProject);

        // assert
        Optional<Project> foundProject = projectRepository.findProjectByName(newProject.getProjectName());
        assertTrue(foundProject.isPresent());
        assertEquals("testProject", foundProject.get().getProjectName());
        assertEquals(2, foundProject.get().getProjectManagerId());
    }


    @Test
    void shouldFetchProjectById() throws SQLException {
        // arrange
        List<Project> allProjects = projectService.getAllProjects();
        Project selectedProject = allProjects.getFirst();


        // act
        Project foundProject = projectService.getProjectById(1);

        // assert
        assertEquals(selectedProject.getId(), foundProject.getId());
    }


    @Test
    void shouldUpdateProjectDetails() throws SQLException {
        // arrange
        List<Project> allProjects = projectService.getAllProjects();
        Project originalProject = allProjects.getFirst();

        int projectId = originalProject.getId();
        String projectName = "testProject";
        String description = "testDescription";
        int projectManagerId = 2;
        LocalDateTime deadline = LocalDateTime.of(2025, 9, 5, 2, 0);
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 5, 2, 0);
        Project newProject = new Project();
        newProject.setId(projectId);
        newProject.setProjectName(projectName);
        newProject.setDescription(description);
        newProject.setProjectManagerId(projectManagerId);
        newProject.setDeadline(deadline);
        newProject.setStatus(Status.IN_PROGRESS);
        newProject.setStartDate(startDate);

        // act
        projectService.updateProject(newProject);
        Project updatedProject = projectService.getProjectById(projectId);

        // assert
        assertNotEquals(originalProject.getProjectName(), updatedProject.getProjectName());
        assertEquals(originalProject.getId(), updatedProject.getId());
    }


    @Test
    void shouldDeleteProjectCorrectly() throws SQLException {
        // arrange
        List<Project> allProjects = projectService.getAllProjects();
        Project projectToBeDeleted = projectRepository.findProjectByName("Alpha").get();

        // act
        projectService.deleteProject(projectToBeDeleted.getId());

        // assert
        assertFalse(projectService.projectExistsByName(projectToBeDeleted.getProjectName()));
    }


}
