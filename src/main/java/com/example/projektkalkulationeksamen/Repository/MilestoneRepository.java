package com.example.projektkalkulationeksamen.Repository;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Mapper.RowMapperUtil;
import com.example.projektkalkulationeksamen.Model.Milestone;
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
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class MilestoneRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MilestoneRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Milestone> getAllMilestones() {
        String sql = "SELECT * FROM milestones";

        return jdbcTemplate.query(sql, RowMapperUtil.milestoneRowMapper());
    }

    public List<Milestone> getMilestonesByProjectId(int projectId) {
        String sql = "SELECT * FROM milestones " +
                "WHERE project_id = ?";

        return jdbcTemplate.query(sql, RowMapperUtil.milestoneRowMapper(), projectId);
    }

    public Optional<Milestone> getMilestoneById(int id) {
        try {
            String sql = "SELECT * FROM milestones WHERE id = ?";

            Milestone milestone = jdbcTemplate.queryForObject(sql, RowMapperUtil.milestoneRowMapper(), id);

            return Optional.of(milestone);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More milestones found with same ID " + id, e);
        }
    }

    public Optional<Milestone> getMilestoneByName(String name) {
        try {
            String sql = "SELECT * FROM milestones WHERE milestone_name = ?";

            Milestone milestone = jdbcTemplate.queryForObject(sql, RowMapperUtil.milestoneRowMapper(), name);

            return Optional.of(milestone);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DatabaseException("More milestones found with same Name " + name, e);
        }
    }

    public Milestone addMilestone(Milestone milestone) {
        try {
            String sql = """
                        INSERT INTO milestones (milestone_name, milestone_description, project_id, deadline)
                        VALUES (?, ?, ?, ?)
                    """;

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, milestone.getMilestoneName());
                ps.setString(2, milestone.getMilestoneDescription());
                ps.setInt(3, milestone.getProjectId());
                if (milestone.getDeadline() != null) {
                    ps.setTimestamp(4, Timestamp.valueOf(milestone.getDeadline()));
                } else {
                    ps.setNull(4, Types.TIMESTAMP);
                }
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();

            if (key == null) {
                throw new DatabaseException("Failed to retrieve generated key from milestone.");
            }

            return getMilestoneById(key.intValue())
                    .orElseThrow(() -> new DatabaseException("Failed to fetch created milestone with ID: " + key.intValue()));

        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create milestone in database", e);
        }
    }

    public boolean deleteMilestone(int id) {
        try {
            String sql = "DELETE FROM milestones WHERE id = ?";

            int affectedRows = jdbcTemplate.update(sql, id);

            return affectedRows > 0;

        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete milestone with ID " + id);
        }
    }

    public boolean updateMilestone(Milestone updatedMilestone) {
        try {

            String sql = "UPDATE milestones SET milestone_name = ?, milestone_description = ?, milestone_status = ?, deadline = ?, completed_at = ? WHERE id = ?";

            Timestamp ifCompleted = null;

            if (updatedMilestone.getCompletedAt() != null) {
                ifCompleted = Timestamp.valueOf(updatedMilestone.getCompletedAt());
            }

            int affectedRows = jdbcTemplate.update(sql,
                    updatedMilestone.getMilestoneName(),
                    updatedMilestone.getMilestoneDescription(),
                    updatedMilestone.getStatus().toString(),
                    updatedMilestone.getDeadline(),
                    ifCompleted,

                    updatedMilestone.getId()
            );

            return affectedRows > 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update milestone with ID " + updatedMilestone.getId());
        }
    }

    public int estimatedHours(int milestoneId) {

        String sql = "SELECT SUM(estimated_hours) FROM tasks WHERE milestone_id = ?";

        Integer estimatedHours = jdbcTemplate.queryForObject(sql, Integer.class, milestoneId);

        if (estimatedHours == null) {
            return 0;
        }
        return estimatedHours;
    }

    public int actualHoursUsed(int milestoneId){
        String sql = "SELECT SUM(actual_hours_used) FROM tasks WHERE milestone_id = ?";

        Integer actualHoursUsed = jdbcTemplate.queryForObject(sql,Integer.class, milestoneId);

        if (actualHoursUsed == null){
            return 0;
        }
        return actualHoursUsed;
    }
}
