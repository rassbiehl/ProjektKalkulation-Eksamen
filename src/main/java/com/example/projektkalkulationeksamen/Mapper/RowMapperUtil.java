package com.example.projektkalkulationeksamen.Mapper;

import com.example.projektkalkulationeksamen.Model.Milestone;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.Status;
import com.example.projektkalkulationeksamen.Model.User;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RowMapperUtil {

    public static RowMapper<User> userRowMapper() {

        return (rs, rowNum) -> {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String password = rs.getString("password_hash");
            LocalDateTime createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
            Role role = Role.valueOf(rs.getString("user_role"));

            return new User(id, username, password, createdAt, role);
        };
    }

    public static RowMapper<Milestone> milestoneRowMapper(){
        return (rs,rowNum) -> {
            int id = rs.getInt("id");
            String name = rs.getString("milestone_name");
            String description = rs.getString("milestone_decription");
            int project_id = rs.getInt("project_id");
            int estimatedHours = rs.getInt("estimated_hours");
            int calculatedCost = rs.getInt("calculated_cost");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            int actualHoursUsed = rs.getInt("actual_hours_used");
            Status status = Status.valueOf(rs.getString("milestone_status"));
            LocalDateTime deadline = rs.getTimestamp("deadline").toLocalDateTime();
            LocalDateTime completedAt = rs.getTimestamp("completed_at").toLocalDateTime();

            return new Milestone(id,name ,description,project_id,estimatedHours,calculatedCost,createdAt,actualHoursUsed,status,deadline,completedAt);
        };
    }
}
