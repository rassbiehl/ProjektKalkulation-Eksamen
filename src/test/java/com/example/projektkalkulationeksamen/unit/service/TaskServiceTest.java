
package com.example.projektkalkulationeksamen.unit.service;

import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Model.Task;
import com.example.projektkalkulationeksamen.Repository.TaskRepository;
import com.example.projektkalkulationeksamen.Service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@Disabled
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTask = new Task(
                1,
                "Test task",
                "testtestest",
                101,
                5,
                2,
                Status.NOT_STARTED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                null

        );
    }

}
