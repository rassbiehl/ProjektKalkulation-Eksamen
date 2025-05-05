package com.example.projektkalkulationeksamen.Mapper;

import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.User;
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
}
