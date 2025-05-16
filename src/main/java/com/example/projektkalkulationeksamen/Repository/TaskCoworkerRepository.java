package com.example.projektkalkulationeksamen.Repository;

import com.example.projektkalkulationeksamen.Exceptions.database.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskCoworkerRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskCoworkerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public boolean addCoworkerToTask (int taskId, int userId) {
        try {
            String sql = "INSERT INTO task_coworkers (user_id, task_id) " +
                    "VALUES (?, ?)";

            int affectedRows = jdbcTemplate.update(sql, userId, taskId);

            return affectedRows > 0;

        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to add coworker with ID: " + userId + " to task with ID: " + taskId , e);
        }
    }


    public boolean removeCoworkerFromTask(int taskId, int userId) {
        try {
            String sql = "DELETE FROM task_coworkers " +
                    "WHERE task_id = ? AND user_id = ?";

            int affectedRows = jdbcTemplate.update(sql, taskId, userId);

            return affectedRows > 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete coworker with ID: " + userId + " to task with ID: " + taskId, e);
        }
    }


    public List<Integer> getAllCoworkersIdsForTask (int taskId) {
            String sql = "SELECT user_id FROM task_coworkers " +
                    "WHERE task_id = ?";

            return jdbcTemplate.queryForList(sql, Integer.class, taskId);
    }

    public List<Integer> getAllTaskIdsForCoworker (int userId) {
        String sql = "SELECT task_id FROM task_coworkers " +
                "WHERE user_id = ?";

        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    public boolean exists(int taskId, int userId) {
        String sql = "SELECT COUNT(*) FROM task_coworkers WHERE task_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, taskId, userId);

        return count != null && count > 0;
    }



}
