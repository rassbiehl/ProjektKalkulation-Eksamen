package com.example.projektkalkulationeksamen.Repository;

import com.example.projektkalkulationeksamen.Exceptions.DatabaseException;
import com.example.projektkalkulationeksamen.Mapper.RowMapperUtil;
import com.example.projektkalkulationeksamen.Model.Milestone;
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

    public Optional<Milestone> getmilestoneById(int id) {
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
            String sql = "INSERT INTO milestones (milestone_name,milestone_description,project_id,estimated_hours,calculated_cost,actual_hours_used,milestone_status,deadline,completed_at) VALUES(?,?,?,?,?,?,?,?,?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                ps.setString(1, milestone.getMilestoneName());
                ps.setString(2, milestone.getMilestoneDescription());
                ps.setInt(3, milestone.getProjectId());
                ps.setInt(4, milestone.getEstimatedHours());
                ps.setInt(5, milestone.getCalculatedCost());
                ps.setInt(6, milestone.getActualHoursUsed());
                ps.setString(7, milestone.getStatus().toString());
                ps.setString(8, milestone.getDeadline().toString());
                ps.setString(9, milestone.getCompleted_at().toString());
                return ps;
            }, keyHolder);

            int generatedId = keyHolder.getKey().intValue();

            Optional<Milestone> optionalMilestone = getmilestoneById(generatedId);

            if (optionalMilestone.isPresent()) {
                return optionalMilestone.get();
            } else {
                throw new DatabaseException("Failed to retrieve created milestone with ID " + generatedId);
            }
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to create milestone in Database ", e);
        }
    }

    public boolean deleteMilestone(int id){
        try {
            String sql = "DELETE FROM milestones WHERE id = ?";

            int affectedRows = jdbcTemplate.update(sql,id);

            return affectedRows > 0;

        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to delete milestone with ID " + id);
        }
    }
}
