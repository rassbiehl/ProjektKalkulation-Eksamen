package com.example.projektkalkulationeksamen.DTO;

import com.example.projektkalkulationeksamen.Model.Status;

import java.time.LocalDateTime;
import java.util.List;

public class TaskDTO {
    private int id;
    private String taskName;
    private String taskDescription;
    private int milestoneId;
    private int estimatedHours;
    private int actualHoursUsed;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDateTime deadline;
    private LocalDateTime completedAt;
    private List<Integer> coworkerIds;

    public TaskDTO(int id, String taskName, String taskDescription, int milestoneId, int estimatedHours, int actualHoursUsed, Status status, LocalDateTime createdAt, LocalDateTime startDate, LocalDateTime deadline, LocalDateTime completedAt, List<Integer> coworkerIds) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.milestoneId = milestoneId;
        this.estimatedHours = estimatedHours;
        this.actualHoursUsed = actualHoursUsed;
        this.status = status;
        this.createdAt = createdAt;
        this.startDate = startDate;
        this.deadline = deadline;
        this.completedAt = completedAt;
        this.coworkerIds = coworkerIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
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

    public void setTaskStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
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

    public List<Integer> getCoworkerIds() {
        return coworkerIds;
    }

    public void setCoworkerIds(List<Integer> coworkerIds) {
        this.coworkerIds = coworkerIds;
    }
}