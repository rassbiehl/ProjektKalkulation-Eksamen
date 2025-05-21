package com.example.projektkalkulationeksamen.controller;

import com.example.projektkalkulationeksamen.dto.ProjectDTO;
import com.example.projektkalkulationeksamen.exceptions.project.ProjectCreationException;
import com.example.projektkalkulationeksamen.model.Project;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.Status;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.service.AuthService;
import com.example.projektkalkulationeksamen.service.ProjectService;
import com.example.projektkalkulationeksamen.service.UserService;
import com.example.projektkalkulationeksamen.validation.SessionValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.param;

import java.time.LocalDateTime;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    private MockHttpSession session;
    private Project testProject;
    private User testUser;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SessionValidation sessionValidation;

    @MockBean
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testName");
        testUser.setPasswordHash("testPassword");
        testUser.setRole(Role.PROJECTMANAGER);

        testProject = new Project();
        testProject.setId(100);
        testProject.setProjectName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setProjectManagerId(testUser.getId());
        testProject.setStartDate(LocalDateTime.now());
        testProject.setDeadline(LocalDateTime.now().plusDays(30));
        testProject.setStatus(Status.NOT_STARTED);

        session = new MockHttpSession();
        session.setAttribute("userId", testUser.getId());
        session.setAttribute("user_role", testUser.getRole());
    }


    @Test
    void getStartPage_shouldRedirectToLogin_whenSessionIsInvalid() throws Exception {
        // mockito behavior
        Mockito.when(sessionValidation.isSessionValid(session)).thenReturn(false);

        // act
        mockMvc.perform(get("/projects/adminStartpage").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loginform"));
    }

    @Test
    void saveProject_shouldRedirectToStartPage_whenProjectCreated() throws Exception {
        // Arrange and mockito behavior
        session.setAttribute("userId", 1);
        Mockito.when(sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)).thenReturn(true);
        Mockito.when(userService.getUserById(1)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/projects/save")
                        .session(session)
                        .param("projectName", testProject.getProjectName())
                        .param("description", testProject.getDescription())
                        .param("startDate", testProject.getStartDate().toString())
                        .param("deadline", testProject.getDeadline().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/projectmanagerStartpage"));
    }


    @Test
    void saveProject_shouldRedirectToAddPage_whenCreationFails() throws Exception {
        // arrange and mockito behavior
        session.setAttribute("userId", 1);
        Mockito.when(sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)).thenReturn(true);
        Mockito.when(userService.getUserById(1)).thenReturn(testUser);
        Mockito.doThrow(new ProjectCreationException("Failed to add project"))
                .when(projectService)
                .addProject(Mockito.any());


        // act and assert
        mockMvc.perform(post("/projects/save")
                .session(session)
                .param("projectName", testProject.getProjectName())
                .param("description", testProject.getDescription())
                .param("startDate", testProject.getStartDate().toString())
                .param("deadline", testProject.getDeadline().toString()))
                .andExpect(redirectedUrl("/projects/add"));
    }

    @Test
    void deleteProject_shouldThrowAccessDenied_whenNotOwner() throws Exception {
        // arrange and mockito behavior
        Mockito.when(sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)).thenReturn(true);
        ProjectDTO project = new ProjectDTO();
        project.setProjectManagerId(99); // not same as userId
        Mockito.when(projectService.getProjectWithDetails(1)).thenReturn(project);
        Mockito.when(userService.getUserById(1)).thenReturn(testUser);

        // act and assert
        mockMvc.perform(post("/projects/delete/1").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveProject_shouldReturn403_whenSessionInvalid() throws Exception {
        // mockito behavior
        Mockito.when(sessionValidation.isSessionValid(session, Role.PROJECTMANAGER)).thenReturn(false);

        // act and assert
        mockMvc.perform(post("/projects/save")
                        .session(session)
                        .param("projectName", testProject.getProjectName())
                        .param("description", testProject.getDescription())
                        .param("startDate", testProject.getStartDate().toString())
                        .param("deadline", testProject.getDeadline().toString()))
                .andExpect(status().isForbidden());
    }
}
