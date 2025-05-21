-- drop existing tables
DROP TABLE IF EXISTS task_coworkers;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS milestones;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;

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


-- Insert: Users
INSERT INTO users (username, password_hash, user_role) VALUES
                                                           ('admin', 'hashed_pass_1', 'ADMIN'),
                                                           ('projectmanager', 'hashed_pass_2', 'PROJECTMANAGER'),
                                                           ('employee', 'hashed_pass_3', 'EMPLOYEE');

-- Insert: Projects
INSERT INTO projects (project_name, project_description, project_manager_id, project_status, deadline, start_date)
VALUES
    ('Alpha', 'Alpha project desc', 2, 'IN_PROGRESS', '2025-07-01', '2025-05-01'),
    ('Beta', 'Beta project desc', 2, 'NOT_STARTED', '2025-08-15', '2025-06-01'),
    ('Gamma', 'Gamma project desc', 2, 'COMPLETED', '2025-04-01', '2025-01-01');

-- Insert: Milestones
INSERT INTO milestones (milestone_name, milestone_description, project_id, milestone_status, deadline)
VALUES
    ('Design', 'Design milestone', 1, 'IN_PROGRESS', '2025-06-01'),
    ('Development', 'Dev milestone', 2, 'NOT_STARTED', '2025-07-20'),
    ('Testing', 'Testing milestone', 3, 'COMPLETED', '2025-03-20');

-- Insert: Tasks
INSERT INTO tasks (task_name, task_description, milestone_id, estimated_hours, actual_hours_used, task_status, start_date, deadline)
VALUES
    ('Create UI', 'Build UI components', 1, 10, 3, 'IN_PROGRESS', '2025-05-10', '2025-05-30'),
    ('Setup DB', 'Initialize database', 2, 8, 0, 'NOT_STARTED', NULL, '2025-07-15'),
    ('Run Tests', 'Automated testing', 3, 5, 6, 'COMPLETED', '2025-02-15', '2025-03-01');

-- Insert: Task coworkers
INSERT INTO task_coworkers (user_id, task_id)
VALUES
    (3, 1),
    (3, 2),
    (3, 3);