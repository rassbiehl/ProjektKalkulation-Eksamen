package com.example.projektkalkulationeksamen.controller;

import com.example.projektkalkulationeksamen.exceptions.security.AuthenticationFailedException;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.service.AuthService;
import com.example.projektkalkulationeksamen.service.UserService;
import com.example.projektkalkulationeksamen.validation.SessionValidation;
import jakarta.servlet.http.HttpSession;
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

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    private MockHttpSession session;
    private User testUser;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private SessionValidation sessionValidation;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testName");
        testUser.setPasswordHash("testPassword");
        testUser.setRole(Role.ADMIN);

        session = new MockHttpSession();
    }


    @Test
    void login_shouldRedirectToStartPage_whenInputIsCorrect() throws Exception {
        // Mockito behavior
        Mockito.when(authService.login("testName", "testPassword")).thenReturn(testUser);
// act and assert
        mockMvc.perform(post("/login")
                        .param("username", "testName")
                        .param("rawPassword", "testPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/adminStartpage"));

    }

    @Test
    void login_shouldFailAndReturnToLoginform_whenCredentialsInvalid() throws Exception {
        // Mockito behavior
        Mockito.when(authService.login("incorrectUsername", "incorrectPassword"))
                .thenThrow(new AuthenticationFailedException("Invalid input"));

        // act and assert
        mockMvc.perform(post("/login")
                        .param("username", "incorrectUsername")
                        .param("rawPassword", "incorrectPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loginform"));
    }

    @Test
    void adminRegister_shouldCreateUserAndRedirect_whenDataValid() throws Exception {
        // arrange
        User user = testUser;

        // mock behavior
        Mockito.doNothing().when(authService)
                .adminRegister(user.getUsername(), user.getPasswordHash(), user.getRole());

        Mockito.when(userService.getUserByUsername(user.getUsername())).thenReturn(user);

        Mockito.when(sessionValidation.isSessionValid(Mockito.any(HttpSession.class), Mockito.eq(Role.ADMIN)))
                .thenReturn(true);
        session.setAttribute("userId", 1);
        session.setAttribute("user_role", Role.ADMIN);

        // act & assert
        mockMvc.perform(post("/register")
                        .param("username", user.getUsername())
                        .param("rawPassword", user.getPasswordHash())
                        .param("role", user.getRole().name())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registerform"));
    }

    @Test
    void registerForm_shouldThrowAccessDenied_whenUserIsNotAdmin() throws Exception {
        // arrange
        User employee = testUser;
        employee.setRole(Role.EMPLOYEE);
        session.setAttribute("userId", employee.getId());
        session.setAttribute("user_role", employee.getRole());

        Mockito.when(sessionValidation.isSessionValid(session, Role.EMPLOYEE)).thenReturn(false);

        // mockito behavior
        mockMvc.perform(get("/registerform")
                        .session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateForm_shouldThrowAccessDenied_whenUserIsNotAdmin() throws Exception {
        // arrange
        User employee = testUser;
        employee.setRole(Role.EMPLOYEE);
        session.setAttribute("userId", employee.getId());
        session.setAttribute("user_role", employee.getRole());

        Mockito.when(sessionValidation.isSessionValid(session, Role.EMPLOYEE)).thenReturn(false);

        // mockito behavior
        mockMvc.perform(get("/updateform/{id}", 1234)
                        .session(session))
                .andExpect(status().isForbidden());
    }

}