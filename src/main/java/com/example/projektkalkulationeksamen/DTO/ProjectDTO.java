package com.example.projektkalkulationeksamen.DTO;

import com.example.projektkalkulationeksamen.Model.Status;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {
    private int id;
    private String projectName;
    private String description;
    private LocalDateTime createdAt;
    private int projectManagerId;
    private int actualHoursUsed;
    private int estimatedHours;
    private int calculatedCost;
    private Status status;
    private LocalDateTime deadline;
    private LocalDateTime startDate;
    private LocalDateTime completedAt;
    private List<MilestoneDTO> milestones;


    public ProjectDTO(int id, String projectName, String description, LocalDateTime createdAt, int projectManagerId, int actualHoursUsed, int estimatedHours, int calculatedCost, Status status, LocalDateTime deadline, LocalDateTime startDate, LocalDateTime completedAt, List<MilestoneDTO> milestones) {
        this.id = id;
        this.projectName = projectName;
        this.description = description;
        this.createdAt = createdAt;
        this.projectManagerId = projectManagerId;
        this.actualHoursUsed = actualHoursUsed;
        this.estimatedHours = estimatedHours;
        this.calculatedCost = calculatedCost;
        this.status = status;
        this.deadline = deadline;
        this.startDate = startDate;
        this.completedAt = completedAt;
        this.milestones = milestones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getProjectManagerId() {
        return projectManagerId;
    }

    public void setProjectManagerId(int projectManagerId) {
        this.projectManagerId = projectManagerId;
    }

    public int getActualHoursUsed() {
        return actualHoursUsed;
    }

    public void setActualHoursUsed(int actualHoursUsed) {
        this.actualHoursUsed = actualHoursUsed;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public int getCalculatedCost() {
        return calculatedCost;
    }

    public void setCalculatedCost(int calculatedCost) {
        this.calculatedCost = calculatedCost;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<MilestoneDTO> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<MilestoneDTO> milestones) {
        this.milestones = milestones;
    }
}
