package com.example.projektkalkulationeksamen.Model;

import java.time.LocalDateTime;

public class Milestone {
private int id;
private String milestoneName;
private String milestoneDescription;
private int projectId;
private int estimatedHours;
private int calculatedCost;
private LocalDateTime createdAt;
private int actualHoursUsed;
private Status status;
private LocalDateTime deadline;
private LocalDateTime completed_at;

    public Milestone(int id, String milestoneName, String milestoneDescription, int projectId,
                     int estimatedHours, int calculatedCost, LocalDateTime createdAt, int actualHoursUsed,
                     Status status, LocalDateTime deadline, LocalDateTime completed_at) {
        this.id = id;
        this.milestoneName = milestoneName;
        this.milestoneDescription = milestoneDescription;
        this.projectId = projectId;
        this.estimatedHours = estimatedHours;
        this.calculatedCost = calculatedCost;
        this.createdAt = createdAt;
        this.actualHoursUsed = actualHoursUsed;
        this.status = status;
        this.deadline = deadline;
        this.completed_at = completed_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    public void setMilestoneDescription(String milestoneDescription) {
        this.milestoneDescription = milestoneDescription;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getActualHoursUsed() {
        return actualHoursUsed;
    }

    public void setActualHoursUsed(int actualHoursUsed) {
        this.actualHoursUsed = actualHoursUsed;
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

    public LocalDateTime getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(LocalDateTime completed_at) {
        this.completed_at = completed_at;
    }
}
