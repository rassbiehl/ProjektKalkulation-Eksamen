package com.example.projektkalkulationeksamen.Repository;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Model.Role;
import com.example.projektkalkulationeksamen.Model.User;
import com.example.projektkalkulationeksamen.mapper.RowMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new DatabaseException("Could not find user with ID " + id, e);
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
            throw new DatabaseException("Could not find user with Username " + username, e);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More users found with same Username " + username, e);
        }
    }

    public User addUser(User user) {
        try {
            String sql = "INSERT INTO users (username,password_hash,role) VALUES (?,?,?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPasswordHash());
                ps.setString(3, user.getRole().toString());
                return ps;
            }, keyHolder);
            int generatedId = keyHolder.getKey().intValue();

            Optional<User> optionalUser = getUserById(generatedId);

            if (optionalUser.isPresent()) {
                return optionalUser.get();
            } else {
                throw new DatabaseException("Failed to retrieve created user with ID " + generatedId);
            }
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to create user in Database ", e);
        }
    }

    public boolean deleteUser(int id) {
        try {
            String sql = "DELETE FROM users WHERE id = ?";
            int affectedRows = jdbcTemplate.update(sql, id);

            return affectedRows > 0;
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to delete user with ID " + id);
        }
    }
}

