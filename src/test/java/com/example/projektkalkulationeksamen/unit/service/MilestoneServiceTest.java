package com.example.projektkalkulationeksamen.unit.service;

import com.example.projektkalkulationeksamen.dto.TaskDTO;
import com.example.projektkalkulationeksamen.exceptions.database.DatabaseException;
import com.example.projektkalkulationeksamen.exceptions.database.DeletionException;
import com.example.projektkalkulationeksamen.exceptions.milestone.MilestoneCreationException;
import com.example.projektkalkulationeksamen.exceptions.milestone.MilestoneUpdateException;
import com.example.projektkalkulationeksamen.exceptions.notfound.MilestoneNotFoundException;
import com.example.projektkalkulationeksamen.exceptions.project.ProjectUpdateException;
import com.example.projektkalkulationeksamen.model.Milestone;
import com.example.projektkalkulationeksamen.model.Project;
import com.example.projektkalkulationeksamen.model.Status;
import com.example.projektkalkulationeksamen.repository.MilestoneRepository;
import com.example.projektkalkulationeksamen.service.MilestoneService;
import com.example.projektkalkulationeksamen.service.TaskService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MilestoneServiceTest {
    private Milestone testMilestone;


    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    MilestoneService milestoneService;

    @BeforeEach
    void setUp() {
        testMilestone = new Milestone();
        testMilestone.setId(1);
        testMilestone.setMilestoneName("Testprojekt");
        testMilestone.setMilestoneDescription("En beskrivelse");
        testMilestone.setStatus(Status.IN_PROGRESS);

        LocalDateTime now = LocalDateTime.of(2025, 5, 20, 10, 0);
        testMilestone.setCreatedAt(now);
        testMilestone.setDeadline(now.plusDays(30));
    }


    @Test
    void addMilestone_shouldSaveMilestone_whenValidInput() {
        // arrange
        Milestone milestoneToAdd = testMilestone;
        List<Milestone> milestoneList = new ArrayList<>();

        // Mock behavior
        when(milestoneRepository.addMilestone(milestoneToAdd)).thenAnswer(invocation -> {
            milestoneList.add(milestoneToAdd);
            return milestoneToAdd;
        });

        // act
        milestoneService.addMilestone(milestoneToAdd);

        // assert
        assertEquals(milestoneToAdd, milestoneList.getFirst());

        // Verify
        Mockito.verify(milestoneRepository, Mockito.times(1)).addMilestone(testMilestone);
    }

    @Test
    void addMilestone_shouldThrowException_whenRepositoryFails() {
        // arrange
        Milestone invalidMilestone = testMilestone;

        // mock behavior
        when(milestoneRepository.addMilestone(testMilestone)).thenThrow(new DatabaseException("Failed to create milestone in Database"));

        // act and assert
        Exception exception = assertThrows(MilestoneCreationException.class, () -> milestoneService.addMilestone(invalidMilestone));

        // assert
        assertEquals("Failed to create milestone with name: " + invalidMilestone.getMilestoneName(), exception.getMessage());

        // verify
        Mockito.verify(milestoneRepository, Mockito.times(1)).addMilestone(invalidMilestone);
    }

    @Test
    void updateMilestone_shouldUpdateMilestone_whenValidInput() {

        // arrange
        Milestone milestoneToUpdate = testMilestone;
        List<Milestone> updatedMilestones = new ArrayList<>();

        // Mock behavior
        when(milestoneRepository.updateMilestone(milestoneToUpdate)).thenAnswer(invocation -> {
            return true;
        });

        when(milestoneRepository.getMilestoneById(1)).thenReturn(Optional.of(milestoneToUpdate));

        // act
        milestoneService.updateMilestone(milestoneToUpdate);


        // Verify
        Mockito.verify(milestoneRepository).updateMilestone(milestoneToUpdate);
    }

    @Test
    void updateMilestone_shouldThrowException_whenRepositoryFails() {
        // arrange
        Milestone milestone = testMilestone;

        // Mock behavior
        when(milestoneRepository.updateMilestone(testMilestone)).thenThrow(new DatabaseException("Failed to update milestone in Database"));
        when(milestoneRepository.getMilestoneById(1)).thenReturn(Optional.of(testMilestone));

        // Act & Assert: expect exception
        Exception exception = assertThrows(MilestoneUpdateException.class, () ->
                milestoneService.updateMilestone(testMilestone)
        );

        // Assert
        assertEquals("Database error while updating milestone with ID: " + milestone.getId(), exception.getMessage());

        // Verify
        Mockito.verify(milestoneRepository).updateMilestone(testMilestone);
    }

    @Test
    void deleteMilestone_shouldDeleteMilestone_whenExists() {
        // arrange
        int milestoneId = testMilestone.getId();
        when(milestoneRepository.deleteMilestone(milestoneId)).thenReturn(true);

        // act
        milestoneService.deleteMilestone(milestoneId);

        // verify
        Mockito.verify(milestoneRepository).deleteMilestone(milestoneId);
    }

    @Test
    void deleteMilestone_shouldThrowException_whenMilestoneNotFound() {
        // arrange
        int milestoneId = testMilestone.getId();
        when(milestoneRepository.deleteMilestone(milestoneId)).thenReturn(false);

        // act & assert: expect exception
        Exception exception = assertThrows(MilestoneNotFoundException.class, () -> milestoneService.deleteMilestone(milestoneId));


        // assert
        assertEquals("Failed to delete milestone with ID: " + milestoneId, exception.getMessage());
        // verify
        Mockito.verify(milestoneRepository).deleteMilestone(milestoneId);
    }

    @Test
    void deleteMilestone_shouldThrowException_whenRepositoryFails() {
        // arrange
        int milestoneId = testMilestone.getId();
        when(milestoneRepository.deleteMilestone(milestoneId)).thenThrow(new DatabaseException("Database error. Failed to delete milestone"));

        // act & assert: expect exception
        Exception exception = assertThrows(DeletionException.class, () -> milestoneService.deleteMilestone(milestoneId));


        // assert
        assertEquals("Failed to delete milestone with ID: " + milestoneId, exception.getMessage());
        // verify
        Mockito.verify(milestoneRepository).deleteMilestone(milestoneId);
    }

    @Test
    void getMilestoneProgress_shouldReturnZero_whenNoTasks() {
        // arrange
        int milestoneId = testMilestone.getId();

        // mockito behavior
        when(taskService.getTasksByMilestoneIdWithDetails(milestoneId)).thenReturn(List.of());

        // act
        int progress = milestoneService.getMilestoneProgress(milestoneId);

        // assert
        assertEquals(0, progress);

        // verify
        Mockito.verify(taskService).getTasksByMilestoneIdWithDetails(milestoneId);
    }

    @Test
    void getMilestoneProgress_shouldReturnCorrectPercentage_whenSomeTasksCompleted() {
        // arrange
        int milestoneId = testMilestone.getId();
        TaskDTO completed = Mockito.mock(TaskDTO.class);
        TaskDTO notCompleted = Mockito.mock(TaskDTO.class);
        List<TaskDTO> tasks = List.of(completed, notCompleted, notCompleted);

        // Mockito behavior
        Mockito.when(taskService.getTasksByMilestoneIdWithDetails(milestoneId)).thenReturn(tasks);
        Mockito.when(completed.getStatus()).thenReturn(Status.COMPLETED);
        Mockito.when(notCompleted.getStatus()).thenReturn(Status.IN_PROGRESS);

        // act
        int progress = milestoneService.getMilestoneProgress(milestoneId);
        // assert
        assertEquals(33, progress); // rounded from 33.33

        // verify
        Mockito.verify(taskService).getTasksByMilestoneIdWithDetails(milestoneId);
    }

    @Test
    void getMilestoneProgress_shouldReturn100_whenAllTasksCompleted() {
        // arrange
        int milestoneId = testMilestone.getId();
        TaskDTO completed = Mockito.mock(TaskDTO.class);
        List<TaskDTO> tasks = List.of(completed, completed, completed);

        // Mockito behavior
        Mockito.when(taskService.getTasksByMilestoneIdWithDetails(milestoneId)).thenReturn(tasks);
        Mockito.when(completed.getStatus()).thenReturn(Status.COMPLETED);


        // act
        int progress = milestoneService.getMilestoneProgress(milestoneId);
        // assert
        assertEquals(100, progress);

        // verify
        Mockito.verify(taskService).getTasksByMilestoneIdWithDetails(milestoneId);
    }

}
