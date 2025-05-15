package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.DTO.MilestoneDTO;
import com.example.projektkalkulationeksamen.DTO.TaskDTO;
import com.example.projektkalkulationeksamen.Exceptions.*;
import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Repository.MilestoneRepository;
import com.example.projektkalkulationeksamen.Service.comparators.DeadlineComparator;
import com.example.projektkalkulationeksamen.Validator.ProjectDataValidator;
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
    private final MilestoneRepository milestoneRepository;
    private final static Logger logger = LoggerFactory.getLogger(MilestoneService.class);
    private final TaskService taskService;

    @Autowired
    public MilestoneService(MilestoneRepository milestoneRepository, TaskService taskService) {
        this.milestoneRepository = milestoneRepository;
        this.taskService = taskService;
    }

    public List<Milestone> getAllMilestones() {
        logger.debug("Sends list of all milestones");
        return milestoneRepository.getAllMilestones();
    }

    public List<Milestone> getMilestonesByProjectId(int projectId) {
        logger.info("Sends list of all milestones with project ID " + projectId);

        List<Milestone> milestones = milestoneRepository.getMilestonesByProjectId(projectId);
        if (milestones.isEmpty()) {
            throw new ProjectNotFoundException("Project with ID " + projectId + " was not found");
        }

        milestones.sort(new DeadlineComparator());
        return milestones;
    }

    public Milestone getMilestoneById(int id) {
        try {
            logger.debug("Sends milestone with id " + id);
            Optional<Milestone> foundMilestone = milestoneRepository.getMilestoneById(id);

            return foundMilestone.orElseThrow(() -> new MilestoneNotFoundException("Could not find milestone with ID " + id));
        } catch (DatabaseException e) {
            throw new MilestoneNotFoundException("Could not find milestone with ID " + id, e);
        }
    }

    public Milestone getMilestoneByName(String name) {
        try {
            logger.debug("Finds milestone with name " + name);
            Optional<Milestone> foundMilestone = milestoneRepository.getMilestoneByName(name);

            return foundMilestone.orElseThrow(() -> new MilestoneNotFoundException("Could not find milestone with name " + name));
        } catch (DatabaseException e) {
            throw new MilestoneNotFoundException("Could not find milestone with name " + name, e);
        }
    }

    public Milestone addMilestone(Milestone milestone) {
        logger.debug("Adds milestone with id " + milestone.getId());


        if (milestoneExistsByNameInProject(milestone.getMilestoneName(), milestone.getProjectId())) {
            throw new MilestoneCreationException("Milestone name already taken in this project");
        }
        try {
            ProjectDataValidator.validateName(milestone.getMilestoneName());
            ProjectDataValidator.validateDescription(milestone.getMilestoneDescription());
            return milestoneRepository.addMilestone(milestone);
        } catch (DatabaseException e) {
            logger.error("Failed to add milestone with ID " + milestone.getId(), e);
            throw new MilestoneCreationException("Failed to create milestone with name " + milestone.getMilestoneName());
        }
    }

    public void deleteMilestone(int id) {


        boolean deleted = milestoneRepository.deleteMilestone(id);

        if (!deleted) {
            logger.warn("Failed to delete milestone with ID " + id);
            throw new MilestoneNotFoundException("Failed to delete milestone with ID " + id);
        }
        logger.info("Succesfully deleted milestone with id " + id);
    }

    public void updateMilestone(Milestone milestone) {
        logger.debug("Attempting to update milestone with ID: {}", milestone.getId());

        try {
            ProjectDataValidator.validateName(milestone.getMilestoneName());
            ProjectDataValidator.validateDescription(milestone.getMilestoneDescription());

            Milestone currentMilestone = milestoneRepository.getMilestoneById(milestone.getId())
                    .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found with ID: " + milestone.getId()));


            String newName = milestone.getMilestoneName().trim().toLowerCase();
            String currentName = currentMilestone.getMilestoneName().trim().toLowerCase();

            if (!newName.equals(currentName) &&
                    milestoneExistsByNameInProjectExcludeId(milestone.getMilestoneName(), milestone.getProjectId(), milestone.getId())) {
                throw new MilestoneCreationException("Milestone name already exists for this project");
            }

            if (milestone.getStatus().equals(Status.COMPLETED) && currentMilestone.getCompletedAt() == null) {
                milestone.setCompletedAt(LocalDateTime.now());
            } else if (milestone.getStatus() != Status.COMPLETED) {
                milestone.setCompletedAt(null);
            }

            boolean updated = milestoneRepository.updateMilestone(milestone);

            if (!updated) {
                logger.warn("Cannot update. No milestone found with ID: {}", milestone.getId());
                throw new MilestoneCreationException("Cannot update. No milestone found with ID: " + milestone.getId());
            }

            logger.info("Successfully updated milestone with ID: {}", milestone.getId());
        } catch (DatabaseException e) {
            logger.error("Database error while trying to update milestone with ID: {}", milestone.getId(), e);
            throw new ProjectCreationException("Database error while updating milestone with ID: " + milestone.getId(), e);
        }
    }

        public boolean milestoneExistsByNameInProject (String name,int projectId){

            List<Milestone> allMilestonesForProject = milestoneRepository.getMilestonesByProjectId(projectId);

            for (Milestone milestone : allMilestonesForProject) {
                if (milestone.getMilestoneName().equalsIgnoreCase(name)) {
                    return true;
                }
            }

            return false;
        }

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

        //DTO

        public List<TaskDTO> getOngoingTasksFromMilestone ( int milestoneId){

            MilestoneDTO milestoneDTO = getMilestoneWithDetails(milestoneId);
            List<TaskDTO> allTasksFromMileStone = milestoneDTO.getTasks();

            List<TaskDTO> ongoingTasks = new ArrayList<>();

            for (TaskDTO taskDTO : allTasksFromMileStone) {
                if (taskDTO.getStatus() != Status.COMPLETED) {
                    ongoingTasks.add(taskDTO);
                }
            }

            return ongoingTasks;
        }

        public List<TaskDTO> getCompletedTasksFromMilestone ( int milestoneId){

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

        public int getMilestoneProgress ( int milestoneId){
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

            return (int) Math.round(finishedTasks * 100.0) / tasksWithDetails.size();
        }

        // DTO Object methods
        public MilestoneDTO getMilestoneWithDetails ( int milestoneId){
            List<TaskDTO> milestoneTasksWithDetails = taskService.getTasksByMilestoneIdWithDetails(milestoneId);
            Milestone milestone = getMilestoneById(milestoneId);
            int progress = getMilestoneProgress(milestoneId);
            return new MilestoneDTO(
                    milestone.getId(),
                    milestone.getMilestoneName(),
                    milestone.getMilestoneDescription(),
                    milestone.getProjectId(),
                    milestone.getEstimatedHours(),
                    milestone.getCalculatedCost(),
                    milestone.getActualHoursUsed(),
                    milestone.getStatus(),
                    milestone.getCreatedAt(),
                    milestone.getDeadline(),
                    milestone.getCompletedAt(),
                    milestoneTasksWithDetails,
                    progress
            );
        }

        public List<MilestoneDTO> getMilestonesByProjectIdWithDetails ( int projectId){
            List<MilestoneDTO> milestonesByProjectIdWithDetails = new ArrayList<>();
            List<Milestone> milestonesByProjectId = milestoneRepository.getMilestonesByProjectId(projectId);

            for (Milestone milestone : milestonesByProjectId) {
                milestonesByProjectIdWithDetails.add(getMilestoneWithDetails(milestone.getId()));
            }
            return milestonesByProjectIdWithDetails;
        }

    }
