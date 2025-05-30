package com.example.projektkalkulationeksamen.integration;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.repository.UserRepository;
import com.example.projektkalkulationeksamen.service.AuthService;
import com.example.projektkalkulationeksamen.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:h2init.sql"}
)
public class UserFlowIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;




    @Test
    void shouldRegisterUserSuccessfully() throws SQLException {

        // arrange
        String newUsername = "test";
        String newPassword = "12345678";
        Role newRole = Role.ADMIN;

        // act
        authService.adminRegister(newUsername, newPassword, newRole);

        //assert

        Optional<User> foundUser = userRepository.getUserByUsername("test");
        assertTrue(foundUser.isPresent());
        assertEquals("test", foundUser.get().getUsername());
        assertEquals(Role.ADMIN, foundUser.get().getRole());

    }


    @Test
    void shouldFetchUserById() throws SQLException {
        // arrange
        List<User> allUsers = userService.getAllUsers();
        User selectedUser = allUsers.get(1);

        // act
        User foundUser = userService.getUserById(selectedUser.getId());

        // assert
        assertNotNull(foundUser);
        assertEquals(selectedUser.getId(), foundUser.getId());
    }

    @Test
    void shouldFetchUserByUsername() throws SQLException {
        //arrange
        List<User> allUsers = userService.getAllUsers();
        User selectedUser = userService.getUserByUsername("projectmanager");

        //act
        User foundUser = userService.getUserByUsername("projectmanager");

        // assert
        assertEquals(selectedUser.getId(), foundUser.getId());
        assertNotNull(foundUser);
    }


    @Test
    void shouldUpdateUserDetails() throws SQLException {
        // arrange
        List<User> allUsers = userService.getAllUsers();
        User originalUser = userService.getUserByUsername("projectmanager");


        // act
        authService.adminUpdate(originalUser.getId(), "messi", "1234568910", originalUser.getRole());
        User updatedUser = userService.getUserById(originalUser.getId());
        // assert
        assertNotEquals(originalUser.getUsername(), updatedUser.getUsername());
        assertNotEquals(originalUser.getPasswordHash(), updatedUser.getPasswordHash());
        assertEquals(originalUser.getRole(), updatedUser.getRole());
    }


    @Test
    void shouldDeleteUserCorrectly() throws SQLException {
        // arrange
        String username = "userToBeDeleted";
        authService.adminRegister(username, "password", Role.ADMIN);
        User userToBeDeleted = userService.getUserByUsername(username);

        // act
        userService.deleteUser(userToBeDeleted.getId());

        // assert
        assertFalse(userService.userExistsById(userToBeDeleted.getId()));
    }


}
