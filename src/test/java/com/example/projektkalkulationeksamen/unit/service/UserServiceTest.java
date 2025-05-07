package com.example.projektkalkulationeksamen.unit.service;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.UserCreationException;
import com.example.projektkalkulationeksamen.Exceptions.UserNotFoundException;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.User;
import com.example.projektkalkulationeksamen.Repository.UserRepository;
import com.example.projektkalkulationeksamen.Service.UserService;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private List<User> mockUsers;
    private LocalDateTime createdTime;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @BeforeEach
    void setup() {
        createdTime = LocalDateTime.of(2024, 1, 1, 12, 0);

        mockUsers = new ArrayList<>();
        mockUsers.add(new User(1, "alice", "password1", createdTime, Role.ADMIN));
        mockUsers.add(new User(2, "bob", "password2", createdTime, Role.PROJECTMANAGER));
        mockUsers.add(new User(3, "charlie", "password3", createdTime, Role.EMPLOYEE));
    }


    @Test
    void getAllUsers() {

        // Mock Behavior
        Mockito.when(userRepository.getAllUsers()).thenReturn(mockUsers);

        // Act: Find all attractions
        List<User> allUsers = userService.getAllUsers();
        User bob = allUsers.get(1);
        // Assert
        assertEquals(3, allUsers.size());
        assertEquals(2, bob.getId());
        assertEquals("bob", bob.getUsername());
        assertEquals("password2", bob.getPasswordHash());
        assertEquals(createdTime, bob.getCreatedAt());
        assertEquals(Role.PROJECTMANAGER, bob.getRole());

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).getAllUsers();
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        User bob = mockUsers.get(1);

        // Mock Behavior
        Mockito.when(userRepository.getUserById(2)).thenReturn(Optional.of(bob));

        // Act : Find user by id
        User result = userService.getUserById(2);

        // Assert
        assertEquals(bob, result);

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).getUserById(2);

    }

    @Test
    void getByUserId_shouldThrowUserNotFoundException_whenUserDoesNotExist() {

        //Mock Behavior
        Mockito.when(userRepository.getUserById(99)).thenReturn(Optional.empty());

        //Act and assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(99));

        // Assert
        assertEquals("Failed to find user with ID: 99", exception.getMessage());

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).getUserById(99);
    }


    @Test
    void getUserByUsername_shouldReturnUser_whenUserExists() {
        User bob = mockUsers.get(1);

        // Mock Behavior
        Mockito.when(userRepository.getUserByUsername("bob")).thenReturn(Optional.of(bob));

        // Act : Find user by id
        User result = userService.getUserByUsername("bob");

        // Assert
        assertEquals(bob, result);

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).getUserByUsername("bob");
    }

    @Test
    void getUserByUsername_shouldThrowUserNotFoundException_whenUserDoesNotExist() {

        // Mock Behavior
        Mockito.when(userRepository.getUserByUsername("messi")).thenReturn(Optional.empty());

        // Act and assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("messi"));

        // Assert
        assertEquals("Failed to find user by username: messi", exception.getMessage());
    }


    @Test
    void addUser_shouldAddUser_whenUserIsValid() {
        List<User> users = mockUsers;
        User rasmus = new User(4, "rasmus", "password4", createdTime, Role.ADMIN);

        //Mock Behavior
        Mockito.when(userRepository.addUser(rasmus)).thenAnswer(invocation -> {
            users.add(rasmus);
            return rasmus;
        });

        // Act : Add user
        User addedUser = userService.addUser(rasmus);

        // Assert
        assertEquals(rasmus, mockUsers.get(3));
        assertEquals(rasmus, addedUser);

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).addUser(rasmus);
    }

    @Test
    void addUser_shouldThrowUserCreationException_whenUserIsInvalid() {
        User messi = new User();

        // Mock Behavior
        Mockito.when(userRepository.addUser(messi)).thenThrow(new DatabaseException("Failed to add user"));

        // Act and assert
        Exception exception = assertThrows(UserCreationException.class, () -> userService.addUser(messi));

        // Assert
        assertEquals("Failed to create user", exception.getMessage());

    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserIsValid() {
        List<User> users = mockUsers;

        // Mock behavior
        Mockito.when(userRepository.deleteUser(1)).thenAnswer(invocation -> {
            users.remove(1);
            return true;
        });

        // Act
        userService.deleteUser(1);

        // Assert
        assertEquals(2, users.size());

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).deleteUser(1);
    }

    @Test
    void deleteUser_throwsUserNotFoundException_whenUserIsNotFound() {

        // Mock behavior
        Mockito.when(userRepository.deleteUser(999)).thenThrow(new DatabaseException("Failed to delete user"));

        // Act and assert
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999));

        // assert
        assertEquals("Failed to delete user with ID " + 999, userNotFoundException.getMessage());
    }

    @Test
    void userExistsByUsername_returnsTrue_whenUserIsFound() {
        List<User> users = mockUsers;

        // Mock behavior
        Mockito.when(userRepository.getUserByUsername("bob")).thenReturn(Optional.of(users.get(1)));

        // Act
        boolean exists = userService.userExistsByUsername("bob");

        // Assert
        assertTrue(exists);

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).getUserByUsername("bob");
    }

    @Test
    void userExistsById_returnsTrue_whenUserIsFound() {
        List<User> users = mockUsers;

        // Mock behavior
        Mockito.when(userRepository.getUserById(2)).thenReturn(Optional.of(users.get(1)));

        // Act
        boolean exists = userService.userExistsById(2);

        // Assert
        assertTrue(exists);

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).getUserById(2);

    }
}
