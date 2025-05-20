package com.example.projektkalkulationeksamen.unit.service;


import com.example.projektkalkulationeksamen.exceptions.notfound.UserNotFoundException;
import com.example.projektkalkulationeksamen.exceptions.security.AuthenticationFailedException;
import com.example.projektkalkulationeksamen.exceptions.user.*;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.model.User;
import com.example.projektkalkulationeksamen.repository.UserRepository;
import com.example.projektkalkulationeksamen.service.AuthService;
import com.example.projektkalkulationeksamen.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private List<User> mockUsers;
    private LocalDateTime createdTime;
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Mock
    HttpSession mockSession;

    @Mock
    UserService userService;

    @InjectMocks
    private AuthService authService;


    @BeforeEach
    void setUp() {
        createdTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        passwordEncoder = new BCryptPasswordEncoder();
        mockUsers = new ArrayList<>();
        mockUsers.add(new User(1, "alice", "password1", createdTime, Role.ADMIN));
        mockUsers.add(new User(2, "bob", "password2", createdTime, Role.PROJECTMANAGER));
        mockUsers.add(new User(3, "charlie", "password3", createdTime, Role.EMPLOYEE));
    }

    // adminupdate, logout

    @Test
    void login_shouldThrowException_whenInvalid() {

        // Mock behavior
        when(userService.getUserByUsername("messi")).thenThrow(new UserNotFoundException("Invalid username or password"));
        // Act and assert
        AuthenticationFailedException authenticationFailedException = assertThrows(AuthenticationFailedException.class, () -> authService.login("messi", "messi"));

        // assert
        assertEquals("Invalid username or password", authenticationFailedException.getMessage());
    }

    @Test
    void adminRegister_shouldAddUser_whenAddedUserIsValid() {
        List<User> users = mockUsers;
        User messi = new User("messi", "12345678", Role.ADMIN);

        // Mockito behavior
        when(userService.addUser(any(User.class))).thenAnswer(invocation -> {
            users.add(messi);
            return messi;
        });

        // Act
        authService.adminRegister("messi", "12345678", Role.ADMIN);

        // Assert
        assertEquals(users.get(3), messi);

        // verify
        Mockito.verify(userService, Mockito.times(1)).addUser(any());

    }

    @Test
    void adminRegister_shouldThrowAuthRegisterException_whenAddedUserIsInvalid() {

        User messi = new User();
        // Mock behavior
        when(userService.addUser(any())).thenThrow(new UserCreationException("Failed to add user"));

        // Act and assert
        AuthRegisterException authRegisterException = assertThrows(AuthRegisterException.class, () -> authService.adminRegister("2332", "wedfwe", Role.EMPLOYEE));

        // assert
        assertEquals("Failed to add user", authRegisterException.getMessage());

    }


    @Test
    void adminUpdate_shouldUpdateUser_whenUpdatedUserIsValid() {
        User newUser = mockUsers.get(1);

        User currentUser = new User(1, "admin", "12345678", Role.ADMIN);
        // Mock behavior


        when(userService.getUserById(Mockito.anyInt())).thenReturn(currentUser);

        Mockito.doAnswer(invocation -> {
            newUser.setUsername("messi");
            return null;
        }).when(userService).updateUser(any());

        // act
        authService.adminUpdate(newUser.getId(), "messi", "12345678", Role.ADMIN);

        // assert
        assertEquals("messi", newUser.getUsername());


        // verify
        Mockito.verify(userService, Mockito.times(1)).updateUser(any());
    }


    @Test
    void adminUpdate_shouldThrowException_whenUserNotFound () {
        // Mock behavior
        when(userService.getUserById(Mockito.anyInt()))
                .thenThrow(new UserNotFoundException("User not found"));

        // act and assert
        UserUpdateException userUpdateException  = assertThrows(UserUpdateException.class, () -> authService.adminUpdate(999, "messi", "123456789", Role.ADMIN));


        // assert
        assertEquals("Could not update user. Reason: User not found", userUpdateException.getMessage());

    }

    @Test
    void logout_shouldInvalidateSession() {
        // Act
        authService.logout(mockSession);

        // Assert
        Mockito.verify(mockSession, Mockito.times(1)).invalidate();
    }
}
