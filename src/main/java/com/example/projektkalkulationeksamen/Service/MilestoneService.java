package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.MilestoneCreationException;
import com.example.projektkalkulationeksamen.Exceptions.MilestoneNotFoundException;
import com.example.projektkalkulationeksamen.Exceptions.ProjectNotFoundException;
import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Project;
import com.example.projektkalkulationeksamen.Repository.MilestoneRepository;
import com.example.projektkalkulationeksamen.Repository.ProjectRepository;
import com.example.projektkalkulationeksamen.Validator.Milestonevalidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MilestoneService {
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final static Logger logger = LoggerFactory.getLogger(MilestoneService.class);

    public MilestoneService(MilestoneRepository milestoneRepository, ProjectRepository projectRepository) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
    }

    public List<Milestone> getAllMilestones() {
        logger.debug("Sends list of all milestones");
        return milestoneRepository.getAllMilestones();
    }

    public Milestone getMilestoneById(int id) {
        try {
            logger.debug("Sends milestone with id " + id);
            Optional<Milestone> foundMilestone = milestoneRepository.getmilestoneById(id);

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

        Milestonevalidator.validateMilestone(milestone.getMilestoneName(),
                milestone.getMilestoneDescription(),
                milestone.getEstimatedHours(),
                milestone.getActualHoursUsed());

        Optional<Project> project = projectRepository.getProjectById(milestone.getProjectId());

        if (project.isEmpty()) {
            logger.warn("Project with ID " + milestone.getProjectId() + " not found. Cannot create milestone.");
            throw new ProjectNotFoundException("Project with ID " + milestone.getProjectId() + " could not be found");
        }

        try {
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

    public boolean updateMilestone(Milestone milestone){
        Milestonevalidator.validateMilestone(milestone.getMilestoneName(),
                milestone.getMilestoneDescription(),
                milestone.getEstimatedHours(),
                milestone.getActualHoursUsed());

        Optional<Milestone> toUpdate = milestoneRepository.getmilestoneById(milestone.getId());

        if (toUpdate.isEmpty()){
            logger.warn("Milestone with ID " + milestone.getId() + " Was not found");
            throw new MilestoneNotFoundException("Could not find milestone with ID " + milestone.getId());
        }
        boolean succes = milestoneRepository.updateMilestone(milestone);

        if (succes) {
            logger.info("Milestone was succesfully updated");
        } else {
            logger.error("Failed to update milestone with ID " + milestone.getId());
        }
        return succes;
    }


}
