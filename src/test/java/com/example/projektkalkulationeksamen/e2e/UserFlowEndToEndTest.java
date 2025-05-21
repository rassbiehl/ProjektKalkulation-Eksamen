package com.example.projektkalkulationeksamen.e2e;

import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserFlowEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    private final String username = "employee2";
    private final String password = "pass123";

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM projects");
        jdbcTemplate.update("DELETE FROM users");

        String hashedPassword = "$2a$10$2a0Lfo1IbrTKoX8Ud5ivV.Cy6Yd63NQecrnElLaT15BAxhayshlGm"; // hashed version of pass123


        User employee = new User();
        employee.setUsername(username);
        employee.setPasswordHash(hashedPassword);
        employee.setRole(Role.EMPLOYEE);
        userRepository.addUser(employee);
    }


    @Test
    void testUserCanLoginSuccessfully() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("rawPassword", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/employeeStartpage"));
    }

    @Test
    void testUserCannotLoginWithWrongPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("rawPassword", "invalidpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loginform"));
    }

    @Test
    void testLoginWithUnknownUsername() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "blabalblabadshdsifhdsufhw")
                        .param("rawPassword", "erwehrewurhiewurw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loginform"));
    }


    @Test
    void testAccess() throws Exception {

        // log in as employee
        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                        .param("username", "employee2")
                        .param("rawPassword", "pass123"))
                .andReturn()
                .getRequest()
                .getSession();


        mockMvc.perform(get("/projects/adminStartpage")
                .session(session))
                .andExpect(status().isForbidden());

    }
}

