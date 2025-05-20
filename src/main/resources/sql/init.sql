-- Creates and uses all necessary tables for the AlphaManager application
-- contains users, projects, milestones, tasks and relations between tasks and users.
CREATE DATABASE IF NOT EXISTS AlphaManager;
USE AlphaManager;


-- User table: saves login details and role.
CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_role ENUM('ADMIN','PROJECTMANAGER','EMPLOYEE') NOT NULL,
    PRIMARY KEY (id)
);

-- Project table: saves project details and is assigned to a project manager (user).
CREATE TABLE projects (
    id INT NOT NULL AUTO_INCREMENT,
    project_name VARCHAR(30) NOT NULL,
    project_description TEXT,
    project_manager_id INT NOT NULL,
    project_status ENUM('COMPLETED','IN_PROGRESS','NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deadline DATETIME,
    start_date DATETIME,
    completed_at DATETIME,
    PRIMARY KEY (id),
    FOREIGN KEY (project_manager_id) REFERENCES users(id)
);

-- Milestone table: saves milestone details and is assigned to a project.
CREATE TABLE milestones (
    id INT NOT NULL AUTO_INCREMENT,
    milestone_name VARCHAR(30) NOT NULL,
    milestone_description TEXT,
    project_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    milestone_status ENUM('COMPLETED','IN_PROGRESS','NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
    deadline DATETIME,
    completed_at DATETIME,
    PRIMARY KEY (id),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Tasks: saves task details and is assigned to a milestone.
CREATE TABLE tasks (
    id INT NOT NULL AUTO_INCREMENT,
    task_name VARCHAR(30) NOT NULL,
    task_description TEXT,
    milestone_id INT NOT NULL,
    estimated_hours INT DEFAULT 0,
    actual_hours_used INT DEFAULT 0,
    task_status ENUM('COMPLETED','IN_PROGRESS','NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    start_date DATETIME,
    deadline DATETIME,
    completed_at DATETIME,
    PRIMARY KEY (id),
    FOREIGN KEY (milestone_id) REFERENCES milestones(id) ON DELETE CASCADE
);

-- join table: assigns users for tasks.
CREATE TABLE task_coworkers (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    task_id INT NOT NULL,
    assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

