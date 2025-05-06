package com.example.projektkalkulationeksamen.Mapper;

import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Model.Task;
import com.example.projektkalkulationeksamen.Model.User;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;

public class RowMapperUtil {

    public static RowMapper<User> userRowMapper() {

        return (rs, rowNum) -> {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String password = rs.getString("password_hash");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            Role role = Role.valueOf(rs.getString("user_role"));

            return new User(id, username, password, createdAt, role);
        };

    }
    public static RowMapper<Task> taskRowMapper() {
        return (rs, rowNum) -> {
            int id = rs.getInt("id");
            String name = rs.getString("task_name");
            String description = rs.getString("task_description");
            int milestoneId = rs.getInt("milestone_id");
            int estimatedHours = rs.getInt("estimated_hours");
            int actualHoursUsed = rs.getInt("actual_hours_used");
            Status status = Status.valueOf(rs.getString("task_status"));
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            LocalDateTime startedAt = rs.getTimestamp("started_at").toLocalDateTime();
            LocalDateTime deadline = rs.getTimestamp("deadline").toLocalDateTime();
            LocalDateTime completedAt = rs.getTimestamp("completed_at").toLocalDateTime();

            return new Task(id, name, description, milestoneId, estimatedHours, actualHoursUsed, status, createdAt,
                    startedAt, deadline, completedAt);
        };
    }


}
