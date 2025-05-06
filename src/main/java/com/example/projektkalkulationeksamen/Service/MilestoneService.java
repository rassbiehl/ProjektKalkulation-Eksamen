package com.example.projektkalkulationeksamen.Service;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.MilestoneCreationException;
import com.example.projektkalkulationeksamen.Exceptions.MilestoneNotFoundException;
import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Project;
import com.example.projektkalkulationeksamen.Repository.MilestoneRepository;
import com.example.projektkalkulationeksamen.Repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MilestoneService {
    private final MilestoneRepository milestoneRepository;
    private final static Logger logger = LoggerFactory.getLogger(MilestoneService.class);

    public MilestoneService(MilestoneRepository milestoneRepository) {
        this.milestoneRepository = milestoneRepository;
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
        } catch (DatabaseException e){
            throw new MilestoneNotFoundException("Could not find milestone with name " + name,e);
        }
    }

    public Milestone addMilestone(Milestone milestone){
        logger.debug("Adds milestone with id " + milestone.getId());

        try {
            return milestoneRepository.addMilestone(milestone);
        } catch (DatabaseException e) {
            logger.error("Failed to add milestone with ID " + milestone.getId(),e);
            throw new MilestoneCreationException("Failed to create milestone with name " + milestone.getMilestoneName());
        }
    }

    public void deleteMilestone(int id){
        boolean deleted = milestoneRepository.deleteMilestone(id);

        if (!deleted){
            logger.warn("Failed to delete milestone with ID " + id);
            throw new MilestoneNotFoundException("Failed to delete milestone with ID " + id);
        }
        logger.info("Succesfully deleted milestone with id " + id);
    }

}
