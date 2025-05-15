package com.example.projektkalkulationeksamen.Repository;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Exceptions.TaskNotFoundException;
import com.example.projektkalkulationeksamen.Mapper.RowMapperUtil;
import com.example.projektkalkulationeksamen.Model.Task;
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
public class TaskRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Task addTask(Task task) {
        try {
            String sql = "INSERT INTO tasks (task_name, task_description, milestone_id, estimated_hours, " +
                    " start_date, deadline) " +
                    "VALUES (?, ?, ?, ?, ?, ?   )";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, task.getTaskName());
                ps.setString(2, task.getTaskDescription());
                ps.setInt(3, task.getMilestoneId());
                ps.setInt(4, task.getEstimatedHours());
                ps.setObject(5, task.getStartedDate());
                ps.setObject(6, task.getDeadline());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();

            if (key == null) {
                throw new DatabaseException("Failed to retrieve generated ID for new user");
            }

            int generatedId = key.intValue();

            Optional<Task> optionalTask = getTaskById(generatedId);

            return optionalTask
                    .orElseThrow(() ->
                            new DatabaseException("Failed to retrieve created task with ID " + generatedId)
                    );
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create task in Database ", e);
        }
    }

    public Optional<Task> getTaskById(int id) {
        try {
            String sql = "SELECT * FROM tasks WHERE id = ?";
            Task task = jdbcTemplate.queryForObject(sql, RowMapperUtil.taskRowMapper(), id);
            return Optional.of(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More tasks found with same ID " + id, e);
        }
    }

    public Optional<Task> getTaskByName(String taskName) {
        try {
            String sql = "SELECT * FROM tasks WHERE task_name = ?";

            Task task = jdbcTemplate.queryForObject(sql, RowMapperUtil.taskRowMapper(), taskName);
            return Optional.of(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More tasks found with same name " + taskName, e);
        }
    }

    public List<Task> getAllTasks() {

        String sql = "SELECT * FROM tasks";
        return jdbcTemplate.query(sql, RowMapperUtil.taskRowMapper());
    }

    public List<Task> getTasksByMilestoneId (int milestoneId) {
        String sql = "SELECT * FROM tasks " +
                "WHERE milestone_id = ?";

        return jdbcTemplate.query(sql, RowMapperUtil.taskRowMapper(), milestoneId);
    }

    public boolean deleteTask(int id) {
        try {
            String sql = "DELETE FROM tasks WHERE id = ?";
            int affectedRows = jdbcTemplate.update(sql, id);

            return affectedRows > 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete task with ID " + id, e);
        }
    }

    public boolean updateTask(Task updatedTask) {
        try {
            String sql = "UPDATE tasks SET task_name = ?, task_description = ?, milestone_id = ?, estimated_hours = ?, " +
                    "actual_hours_used = ?, task_status = ?, start_date = ?, deadline = ?, completed_at = ? " +
                    "WHERE id = ?";

            Timestamp ifCompleted = null;
            if (updatedTask.getCompletedAt() != null) {
                ifCompleted = Timestamp.valueOf(updatedTask.getCompletedAt());
            }

            int affectedRows = jdbcTemplate.update(sql,
                    updatedTask.getTaskName(),
                    updatedTask.getTaskDescription(),
                    updatedTask.getMilestoneId(),
                    updatedTask.getEstimatedHours(),
                    updatedTask.getActualHoursUsed(),
                    updatedTask.getStatus().toString(),
                    Timestamp.valueOf(updatedTask.getStartedDate()),
                    Timestamp.valueOf(updatedTask.getDeadline()),
                    ifCompleted,

                    updatedTask.getId()
            );
            return affectedRows > 0;

        } catch (Exception e) {
            throw new DatabaseException("Failed to update task with ID: " + updatedTask.getId(), e);
        }
    }

}


