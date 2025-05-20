package com.example.projektkalkulationeksamen.unit.validator;

import com.example.projektkalkulationeksamen.dto.ProjectDTO;
import com.example.projektkalkulationeksamen.model.Role;
import com.example.projektkalkulationeksamen.validation.AccessValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AccessValidationTest {


    private ProjectDTO projectDTO;
    private int ownerId;
    private int nonOwnerId;
    private Role nonOwnerRole;
    private Role ownerRole;

    @BeforeEach
    void setUp() {
        ownerId = 1;
        nonOwnerId = 2;

        nonOwnerRole = Role.EMPLOYEE;
        ownerRole = Role.PROJECTMANAGER;

        projectDTO = new ProjectDTO();
        projectDTO.setProjectManagerId(ownerId);
    }


    @Test
    void isOwner_shouldReturnFalse_ifUserIsNotOwner() {
        // Act
        boolean isOwner = AccessValidation.isOwner(nonOwnerId, nonOwnerRole, projectDTO);
        // Assert
        assertFalse(isOwner);
    }


    @Test
    void isOwner_shouldReturnTrue_ifUserIsOwner() {

        // Act
        boolean isOwner = AccessValidation.isOwner(ownerId, ownerRole, projectDTO);

        // Assert
        assertTrue(isOwner);

    }
}
