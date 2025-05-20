package com.example.projektkalkulationeksamen.dto;

import com.example.projektkalkulationeksamen.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public class MilestoneDTO {
    private int id;
    private String milestoneName;
    private String milestoneDescription;
    private int projectId;
    private Status milestoneStatus;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private LocalDateTime completedAt;
    private List<TaskDTO> tasks;
    private int progress;

    public MilestoneDTO(int id, String milestoneName, String milestoneDescription, int projectId, Status milestoneStatus, LocalDateTime createdAt, LocalDateTime deadline, LocalDateTime completedAt, List<TaskDTO> tasks, int progress) {
        this.id = id;
        this.milestoneName = milestoneName;
        this.milestoneDescription = milestoneDescription;
        this.projectId = projectId;
        this.milestoneStatus = milestoneStatus;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.completedAt = completedAt;
        this.tasks = tasks;
        this.progress = progress;
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

    public Status getMilestoneStatus() {
        return milestoneStatus;
    }

    public void setMilestoneStatus(Status milestoneStatus) {
        this.milestoneStatus = milestoneStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}