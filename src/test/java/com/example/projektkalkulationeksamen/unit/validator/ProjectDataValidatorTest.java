package com.example.projektkalkulationeksamen.unit.validator;
import com.example.projektkalkulationeksamen.exceptions.project.ProjectCreationException;
import com.example.projektkalkulationeksamen.validation.ProjectDataValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProjectDataValidatorTest {


    @Test
    void validateName_shouldThrowProjectCreationException_whenInvalidName () {
        // Arrange
        String invalidName = " ";

        // Act and assert
        assertThrows(ProjectCreationException.class, () -> ProjectDataValidator.validateName(invalidName));
    }


    @Test
    void validateDescription_shouldThrowProjectCreationException_whenInvalidDescription () {
        // Arrange
        String invalidDescription = " ";
        // Act and assert
        assertThrows(ProjectCreationException.class, () -> ProjectDataValidator.validateDescription(invalidDescription));
    }
}