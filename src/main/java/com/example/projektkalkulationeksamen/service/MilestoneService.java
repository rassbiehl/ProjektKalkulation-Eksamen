package com.example.projektkalkulationeksamen.service;

import com.example.projektkalkulationeksamen.dto.MilestoneDTO;
import com.example.projektkalkulationeksamen.dto.TaskDTO;
import com.example.projektkalkulationeksamen.exceptions.database.DatabaseException;
import com.example.projektkalkulationeksamen.exceptions.database.DeletionException;
import com.example.projektkalkulationeksamen.exceptions.milestone.MilestoneCreationException;
import com.example.projektkalkulationeksamen.exceptions.milestone.MilestoneUpdateException;
import com.example.projektkalkulationeksamen.exceptions.notfound.MilestoneNotFoundException;
import com.example.projektkalkulationeksamen.model.Milestone;
import com.example.projektkalkulationeksamen.model.Status;
import com.example.projektkalkulationeksamen.repository.MilestoneRepository;
import com.example.projektkalkulationeksamen.comparators.DeadlineComparator;
import com.example.projektkalkulationeksamen.validation.ProjectDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MilestoneService {
    private final static Logger logger = LoggerFactory.getLogger(MilestoneService.class);
    private final MilestoneRepository milestoneRepository;
    private final TaskService taskService;

    @Autowired
    public MilestoneService(MilestoneRepository milestoneRepository, TaskService taskService) {
        this.milestoneRepository = milestoneRepository;
        this.taskService = taskService;
    }

    public List<Milestone> getMilestonesByProjectId(int projectId) {
        logger.info("Sends list of all milestones with project ID " + projectId);

        List<Milestone> milestones = milestoneRepository.getMilestonesByProjectId(projectId);

        milestones.sort(new DeadlineComparator());
        return milestones;
    }

    public Milestone getMilestoneById(int milestoneId) {
        try {
            logger.debug("Sends milestone with id " + milestoneId);
            Optional<Milestone> foundMilestone = milestoneRepository.getMilestoneById(milestoneId);

            return foundMilestone.orElseThrow(() -> new MilestoneNotFoundException("Could not find milestone with ID " + milestoneId));
        } catch (DatabaseException e) {
            logger.error("Failed to retrieve milestone with ID: {}", milestoneId);
            throw new MilestoneNotFoundException("Could not find milestone with ID: " + milestoneId, e);
        }
    }

    public void addMilestone(Milestone milestone) {
        logger.debug("Adds milestone with ID: {}", milestone.getId());


        if (milestoneExistsByNameInProject(milestone.getMilestoneName(), milestone.getProjectId())) {
            logger.warn("Failed to add milestone because of duplicated name: {}", milestone.getMilestoneName());
            throw new MilestoneCreationException("Milestone name already taken in this project");
        }
        try {
            ProjectDataValidator.validateName(milestone.getMilestoneName());
            ProjectDataValidator.validateDescription(milestone.getMilestoneDescription());
            Milestone saved = milestoneRepository.addMilestone(milestone);
            logger.info("Milestone created with ID: {}", saved.getId());
        } catch (DatabaseException e) {
            logger.error("Failed to add milestone with ID: {}", milestone.getId(), e);
            throw new MilestoneCreationException("Failed to create milestone with name: " + milestone.getMilestoneName());
        }
    }

    public void deleteMilestone(int milestoneId) {

        try {
            boolean deleted = milestoneRepository.deleteMilestone(milestoneId);

            if (!deleted) {
                logger.warn("Failed to delete milestone with ID " + milestoneId);
                throw new MilestoneNotFoundException("Failed to delete milestone with ID: " + milestoneId);
            }
            logger.info("Succesfully deleted milestone with id " + milestoneId);
        } catch (DatabaseException e) {
            logger.error("Failed to delete milestone with ID: {}", milestoneId, e);
            throw new DeletionException("Failed to delete milestone with ID: " + milestoneId, e);
        }
    }

    public void updateMilestone(Milestone milestone) {
        logger.debug("Attempting to update milestone with ID: {}", milestone.getId());

        try {
            ProjectDataValidator.validateName(milestone.getMilestoneName());
            ProjectDataValidator.validateDescription(milestone.getMilestoneDescription());

            Milestone currentMilestone = milestoneRepository.getMilestoneById(milestone.getId())
                    .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found with ID: " + milestone.getId()));

            String newName = milestone.getMilestoneName().trim();
            String currentName = currentMilestone.getMilestoneName().trim();

            if (!newName.equalsIgnoreCase(currentName) &&
                    milestoneExistsByNameInProjectExcludeId(newName, milestone.getProjectId(), milestone.getId())) {
                logger.warn("Duplicate milestonename: {}", newName);
                throw new MilestoneUpdateException("Milestone name already exists for this project");
            }

            if (milestone.getDeadline() == null) {
                throw new MilestoneUpdateException("Deadline cannot be empty");
            }

            // When changing the milestone status to "COMPLETED", update completed_at to the current LocalDateTime.
            if (milestone.getStatus().equals(Status.COMPLETED) && currentMilestone.getCompletedAt() == null) {
                milestone.setCompletedAt(LocalDateTime.now());
                // If changed back from "COMPLETED" to "In progress" or "Not started" remove the completed_at value.
            } else if (milestone.getStatus() != Status.COMPLETED) {
                milestone.setCompletedAt(null);
            }

            boolean updated = milestoneRepository.updateMilestone(milestone);

            if (!updated) {
                logger.warn("Cannot update. No milestone found with ID: {}", milestone.getId());
                throw new MilestoneUpdateException("Cannot update. No milestone found with ID: " + milestone.getId());
            }

            logger.info("Successfully updated milestone with ID: {}", milestone.getId());
        } catch (DatabaseException e) {
            logger.error("Database error while trying to update milestone with ID: {}", milestone.getId(), e);
            throw new MilestoneUpdateException("Database error while updating milestone with ID: " + milestone.getId(), e);
        }
    }

    // returns true if a milestone name already exists for a milestone linked to a given project id
    public boolean milestoneExistsByNameInProject(String name, int projectId) {

        List<Milestone> allMilestonesForProject = milestoneRepository.getMilestonesByProjectId(projectId);

        for (Milestone milestone : allMilestonesForProject) {
            if (milestone.getMilestoneName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    // Ensures no duplicate names (ignoring its own name when updating)
    public boolean milestoneExistsByNameInProjectExcludeId(String name, int projectId, int excludeId) {
        String target = name.trim();

        List<Milestone> milestones = getMilestonesByProjectId(projectId);

        for (Milestone m : milestones) {
            if (m.getId() != excludeId) {
                String current = m.getMilestoneName().trim();
                if (current.equalsIgnoreCase(target)) {
                    return true;
                }
            }
        }

        return false;
    }

    // returns a list of tasks from a milestone dto object that has the status of "in progress" or "not started".
    public List<TaskDTO> getOngoingTasksFromMilestone(int milestoneId) {
        MilestoneDTO milestoneDTO = getMilestoneWithDetails(milestoneId);
        List<TaskDTO> allTasksFromMileStone = milestoneDTO.getTasks();

        List<TaskDTO> ongoingTasks = new ArrayList<>();

        for (TaskDTO taskDTO : allTasksFromMileStone) {
            if (taskDTO.getStatus() != Status.COMPLETED) {
                ongoingTasks.add(taskDTO);
            }
        }
        logger.debug("Found {} ongoing tasks for milestone {}", ongoingTasks.size(), milestoneId);
        return ongoingTasks;
    }

    public List<TaskDTO> getCompletedTasksFromMilestone(int milestoneId) {

        MilestoneDTO milestoneDTO = getMilestoneWithDetails(milestoneId);
        List<TaskDTO> allTasksFromMileStone = milestoneDTO.getTasks();

        List<TaskDTO> completedTasks = new ArrayList<>();

        for (TaskDTO taskDTO : allTasksFromMileStone) {
            if (taskDTO.getStatus() == Status.COMPLETED) {
                completedTasks.add(taskDTO);
            }
        }

        return completedTasks;
    }

    // Calculates percentage of completed tasks for the given milestone
    public int getMilestoneProgress(int milestoneId) {

        List<TaskDTO> tasksWithDetails = taskService.getTasksByMilestoneIdWithDetails(milestoneId);


        if (tasksWithDetails.isEmpty()) {
            return 0;
        }

        int finishedTasks = 0;

        for (TaskDTO taskDTO : tasksWithDetails) {
            if (taskDTO.getStatus() == Status.COMPLETED) {
                finishedTasks++;
            }
        }

        int progress = (int) Math.round((finishedTasks * 100.0) / tasksWithDetails.size());
        logger.debug("Progress for milestone {}: {}%", milestoneId, progress);
        return progress;
    }

    public MilestoneDTO getMilestoneWithDetails(int milestoneId) {
        List<TaskDTO> milestoneTasksWithDetails = taskService.getTasksByMilestoneIdWithDetails(milestoneId);
        Milestone milestone = getMilestoneById(milestoneId);
        int progress = getMilestoneProgress(milestoneId);
        return new MilestoneDTO(
                milestone.getId(),
                milestone.getMilestoneName(),
                milestone.getMilestoneDescription(),
                milestone.getProjectId(),
                milestone.getStatus(),
                milestone.getCreatedAt(),
                milestone.getDeadline(),
                milestone.getCompletedAt(),
                milestoneTasksWithDetails,
                progress
        );
    }

    // finds all milestones linked to a project ID and transforms them into milestone dto objects
    public List<MilestoneDTO> getMilestonesByProjectIdWithDetails(int projectId) {
        List<MilestoneDTO> milestonesByProjectIdWithDetails = new ArrayList<>();
        List<Milestone> milestonesByProjectId = milestoneRepository.getMilestonesByProjectId(projectId);

        for (Milestone milestone : milestonesByProjectId) {
            milestonesByProjectIdWithDetails.add(getMilestoneWithDetails(milestone.getId()));
        }

        logger.info("Found {} milestones with details for project {}", milestonesByProjectIdWithDetails.size(), projectId);
        return milestonesByProjectIdWithDetails;
    }


    // returns total estimated hours for all tasks linked to a milestone ID
    public int getEstimatedHours(int milestoneId) {

        int hours = milestoneRepository.estimatedHours(milestoneId);

        logger.debug("Estimated hours for milestone {}: {}", milestoneId, hours);
        return hours;

    }

    // returns the total of actual hours used for all tasks linked to a milestone id
    public int getActualHoursUsed(int milestoneId) {
        int hours = milestoneRepository.actualHoursUsed(milestoneId);
        logger.debug("Actual hours for milestone {}: {}", milestoneId, hours);
        return hours;
    }
}
