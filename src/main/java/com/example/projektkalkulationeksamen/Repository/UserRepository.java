package com.example.projektkalkulationeksamen.Repository;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Model.User;
import com.example.projektkalkulationeksamen.Mapper.RowMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAllUsers() {

        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, RowMapperUtil.userRowMapper());
    }

    public Optional<User> getUserById(int id) {
        try {
            String sql = "SELECT * FROM users" +
                    " WHERE id = ?";
            User user = jdbcTemplate.queryForObject(sql, RowMapperUtil.userRowMapper(), id);

            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More users found with same ID " + id, e);
        }
    }

    public Optional<User> getUserByUsername(String username) {
        try {
            String sql = "SELECT * FROM users " +
                    "WHERE username = ?";

            User user = jdbcTemplate.queryForObject(sql, RowMapperUtil.userRowMapper(), username);

            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More users found with same Username " + username, e);
        }
    }

    public User addUser(User user) {
        try {
            String sql = "INSERT INTO users (username,password_hash,user_role) VALUES (?,?,?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });

                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPasswordHash());
                ps.setString(3, user.getRole().toString());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();

            if (key == null) {
                throw new DatabaseException("Failed to retrieve generated ID for new user");
            }

            int generatedId = key.intValue();

            Optional<User> optionalUser = getUserById(generatedId);

            return optionalUser
                    .orElseThrow(() ->
                            new DatabaseException("Failed to retrieve created user with generated ID " + generatedId)
            );

        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create user in Database: ", e);
        }
    }

    public boolean deleteUser(int id) {
        try {
            String sql = "DELETE FROM users WHERE id = ?";
            int affectedRows = jdbcTemplate.update(sql, id);

            return affectedRows > 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete user with ID " + id, e);
        }
    }

    public boolean updateUser (User newUser) {
        try {
            String sql = "UPDATE users set username = ?, password_hash = ?, user_role = ? " +
                    "WHERE id = ?";

            int affectedRows = jdbcTemplate.update(sql,
                    newUser.getUsername(),
                    newUser.getPasswordHash(),
                    newUser.getRole().toString(),

                    newUser.getId()
            );

            return affectedRows > 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update user with ID: " + newUser.getId(), e);
        }
    }
}

