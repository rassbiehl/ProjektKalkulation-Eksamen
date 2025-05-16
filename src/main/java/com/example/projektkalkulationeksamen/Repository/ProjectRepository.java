package com.example.projektkalkulationeksamen.Repository;


import com.example.projektkalkulationeksamen.Exceptions.database.DatabaseException;
import com.example.projektkalkulationeksamen.Mapper.RowMapperUtil;
import com.example.projektkalkulationeksamen.Model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Project> getAllProjects() {
        String sql = "SELECT * FROM projects";
        return jdbcTemplate.query(sql, RowMapperUtil.projectRowMapper());
    }

    public Optional<Project> findProjectById(int id) {
        try {
            String sql = "SELECT * FROM projects " +
                    "WHERE id = ?";

            Project project = jdbcTemplate.queryForObject(sql, RowMapperUtil.projectRowMapper(), id);

            return Optional.of(project);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More projects found with the same ID: " + id, e);
        }
    }

    public List<Project> getAllProjectsByProjectManager(int projectManagerId) {
        String sql = "SELECT * FROM projects " +
                "WHERE project_manager_id = ?";
        return jdbcTemplate.query(sql, RowMapperUtil.projectRowMapper(), projectManagerId);
    }


    public Optional<Project> findProjectByName(String projectName) {
        try {
            String sql = "SELECT * FROM projects " +
                    "WHERE project_name = ?";

            Project project = jdbcTemplate.queryForObject(sql, RowMapperUtil.projectRowMapper(), projectName);

            return Optional.of(project);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More projects found with the same project name: " + projectName, e);
        }
    }

    public Project addProject(Project project) {
        try {
            String sql = "INSERT INTO projects " +
                    "(project_name, project_description, project_manager_id, estimated_hours, deadline, start_date) " +
                    "VALUES(?, ?, ?, ?, ?, ?) ";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

                ps.setString(1, project.getProjectName());
                ps.setString(2, project.getDescription());
                ps.setInt(3, project.getProjectManagerId());
                ps.setInt(4, project.getEstimatedHours());
                ps.setTimestamp(5, Timestamp.valueOf(project.getDeadline()));
                ps.setTimestamp(6, Timestamp.valueOf(project.getStartDate()));
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();

            if (key == null) {
                throw new DatabaseException("Failed to retrieve generated ID for new project");
            }

            int generatedId = key.intValue();

            Optional<Project> optionalProject = findProjectById(generatedId);

            return optionalProject
                    .orElseThrow(() ->
                            new DatabaseException("Failed to retrieve created project with generated ID: " + generatedId)
                    );
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create project in Database: ", e);
        }
    }


    public boolean deleteProject(int id) {
        try {
            String sql = "DELETE FROM projects " +
                    "WHERE id = ?";

            int affectedRows = jdbcTemplate.update(sql, id);

            return affectedRows > 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete project with ID " + id, e);
        }
    }

    public boolean updateProject(Project updatedProject) {
        try {
            String sql = "UPDATE projects set project_name = ?, project_description = ?, project_manager_id = ?, actual_hours_used = ?, estimated_hours = ?, calculated_cost = ?, project_status = ?, deadline = ?, start_date = ?, completed_at = ? " +
                    "WHERE id = ?";

            Timestamp ifCompleted = null;

            if (updatedProject.getCompletedAt() != null) {
                ifCompleted = Timestamp.valueOf(updatedProject.getCompletedAt());
            }

            int affectedRows = jdbcTemplate.update(sql,
                    updatedProject.getProjectName(),
                    updatedProject.getDescription(),
                    updatedProject.getProjectManagerId(),
                    updatedProject.getActualHoursUsed(),
                    updatedProject.getEstimatedHours(),
                    updatedProject.getCalculatedCost(),
                    updatedProject.getStatus().toString(),
                    Timestamp.valueOf(updatedProject.getDeadline()),
                    Timestamp.valueOf(updatedProject.getStartDate()),
                    ifCompleted,

                    updatedProject.getId()
            );

            return affectedRows > 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update with project ID: " + updatedProject.getId(), e);
        }
    }


}
