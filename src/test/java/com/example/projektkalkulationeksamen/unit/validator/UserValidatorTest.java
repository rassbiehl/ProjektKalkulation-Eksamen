package com.example.projektkalkulationeksamen.unit.validator;

import com.example.projektkalkulationeksamen.exceptions.user.UserCreationException;
import com.example.projektkalkulationeksamen.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {


    @Test
    void validateUsername_shouldThrowUserCreationException_whenInvalidUsername () {
        // Arrange
        String invalidUsername = "";

        // Act and assert
        UserCreationException userCreationException = assertThrows(UserCreationException.class, () -> UserValidator.validateUsername(invalidUsername));

    }


    @Test
    void validatePassword_shouldThrowUserCreationException_whenInvalidPassword () {
        // Arrange
        String invalidPassword = "123";

        // act and assert
        UserCreationException userCreationException = assertThrows(UserCreationException.class, () -> UserValidator.validatePassword(invalidPassword));
    }

}
