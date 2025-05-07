-- drop existing tables
DROP TABLE IF EXISTS task_coworkers;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS milestones;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;

-- creating database
CREATE TABLE users (
                       id INT NOT NULL AUTO_INCREMENT,
                       username VARCHAR(30) NOT NULL UNIQUE,
                       password_hash VARCHAR(200) NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       user_role ENUM('ADMIN','PROJECTMANAGER','EMPLOYEE') NOT NULL,
                       PRIMARY KEY (id)
);

CREATE TABLE projects (
                          id INT NOT NULL AUTO_INCREMENT,
                          project_name VARCHAR(30) NOT NULL,
                          project_description TEXT,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          project_manager_id INT NOT NULL,
                          actual_hours_used INT DEFAULT 0,
                          estimated_hours INT DEFAULT 0,
                          calculated_cost INT DEFAULT 0,
                          project_status ENUM('COMPLETED','IN_PROGRESS','NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
                          deadline DATETIME NOT NULL,
                          start_date DATETIME NOT NULL,
                          completed_at DATETIME,
                          PRIMARY KEY (id),
                          FOREIGN KEY (project_manager_id) REFERENCES users(id)
);

CREATE TABLE milestones (
                            id INT NOT NULL AUTO_INCREMENT,
                            milestone_name VARCHAR(30) NOT NULL,
                            milestone_description TEXT,
                            project_id INT NOT NULL,
                            estimated_hours INT DEFAULT 0,
                            calculated_cost INT DEFAULT 0,
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            actual_hours_used INT DEFAULT 0,
                            milestone_status ENUM('COMPLETED','IN_PROGRESS','NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
                            deadline DATETIME,
                            completed_at DATETIME,
                            PRIMARY KEY (id),
                            FOREIGN KEY (project_id) REFERENCES projects(id)
);

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
                       FOREIGN KEY (milestone_id) REFERENCES milestones(id)
);

CREATE TABLE task_coworkers (
                                id INT NOT NULL AUTO_INCREMENT,
                                user_id INT NOT NULL,
                                task_id INT NOT NULL,
                                assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (id),
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);


-- Insert
INSERT INTO users (username, password_hash, user_role) VALUES
                                                           ('admin', 'hashed_pass_1', 'ADMIN'),
                                                           ('projectmanager', 'hashed_pass_2', 'PROJECTMANAGER'),
                                                           ('employee', 'hashed_pass_3', 'EMPLOYEE');
