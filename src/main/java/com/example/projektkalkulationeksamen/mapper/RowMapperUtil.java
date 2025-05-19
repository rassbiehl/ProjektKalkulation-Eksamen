package com.example.projektkalkulationeksamen.mapper;

import com.example.projektkalkulationeksamen.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
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

    public static RowMapper<Milestone> milestoneRowMapper(){
        return (rs,rowNum) -> {
            int id = rs.getInt("id");
            String name = rs.getString("milestone_name");
            String description = rs.getString("milestone_description");
            int project_id = rs.getInt("project_id");
            int estimatedHours = rs.getInt("estimated_hours");
            int calculatedCost = rs.getInt("calculated_cost");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            int actualHoursUsed = rs.getInt("actual_hours_used");
            Status status = Status.valueOf(rs.getString("milestone_status"));
            //Null safe handler af deadline
            LocalDateTime deadline = null;
            Timestamp deadlineTs = rs.getTimestamp("deadline");
            if(deadlineTs != null){
                deadline = deadlineTs.toLocalDateTime();
            }
            LocalDateTime completedAt = null;
            Timestamp completedAtTs = rs.getTimestamp("completed_at");
            if (completedAtTs != null){
                completedAt = completedAtTs.toLocalDateTime();
            }


            return new Milestone(id,name ,description,project_id,estimatedHours,calculatedCost,createdAt,actualHoursUsed,status,deadline,completedAt);
        };
    }

    public static RowMapper<Project> projectRowMapper() {
        return (rs, rowNum) -> {
            int id = rs.getInt("id");
            String projectName = rs.getString("project_name");
            String description = rs.getString("project_description");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            int projectManagerId = rs.getInt("project_manager_id");
            int actualHoursUsed = rs.getInt("actual_hours_used");
            int estimatedHours = rs.getInt("estimated_hours");
            int calculatedCost = rs.getInt("calculated_cost");
            Status status = Status.valueOf(rs.getString("project_status"));

            LocalDateTime deadline = null;
            Timestamp deadlineTs = rs.getTimestamp("deadline");
            if (deadlineTs != null){
                deadline = deadlineTs.toLocalDateTime();
            }

            LocalDateTime startDate = null;
            Timestamp startDateTs = rs.getTimestamp("start_date");
            if (startDateTs != null){
                startDate = startDateTs.toLocalDateTime();
            }

            LocalDateTime completedAt = null;
            Timestamp completedAtTs = rs.getTimestamp("completed_at");
            if (completedAtTs != null){
                completedAt = completedAtTs.toLocalDateTime();
            }
            return new Project(id, projectName, description, createdAt, projectManagerId,actualHoursUsed, estimatedHours, calculatedCost, status, deadline, startDate, completedAt);
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
            LocalDateTime startedAt = null;
            Timestamp startedAtTs = rs.getTimestamp("start_date");
            if (startedAtTs != null){
                startedAt = startedAtTs.toLocalDateTime();
            }

            Timestamp deadlineTs = rs.getTimestamp("deadline");
            LocalDateTime deadline = null;
            if (deadlineTs != null){
                deadline = deadlineTs.toLocalDateTime();
            }

            Timestamp completedAtTimestamp = rs.getTimestamp("completed_at");
            LocalDateTime completedAt = null;

            if (completedAtTimestamp != null) {
                completedAt = completedAtTimestamp.toLocalDateTime();
            }

            return new Task(id, name, description, milestoneId, estimatedHours, actualHoursUsed, status, createdAt,
                    startedAt, deadline, completedAt);
        };
    }

    public static RowMapper<Integer> estimatedHours() {
        return (rs, rowNum) -> rs.getInt("estimated_hours");
    }


}
