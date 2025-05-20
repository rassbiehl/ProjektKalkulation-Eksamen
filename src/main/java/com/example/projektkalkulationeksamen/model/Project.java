package com.example.projektkalkulationeksamen.model;

import java.time.LocalDateTime;

public class Project {
    private int id;
    private String projectName;
    private String description;
    private LocalDateTime createdAt;
    private int projectManagerId;
    private Status status;
    private LocalDateTime deadline;
    private LocalDateTime startDate;
    private LocalDateTime completedAt;

    public Project(int id, String projectName, String description, LocalDateTime createdAt, int projectManagerId, Status status, LocalDateTime deadline, LocalDateTime startDate, LocalDateTime completedAt) {
        this.id = id;
        this.projectName = projectName;
        this.description = description;
        this.createdAt = createdAt;
        this.projectManagerId = projectManagerId;
        this.status = status;
        this.deadline = deadline;
        this.startDate = startDate;
        this.completedAt = completedAt;
    }

    public Project() {
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
}
