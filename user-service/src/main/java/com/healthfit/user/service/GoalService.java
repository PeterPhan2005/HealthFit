package com.healthfit.user.service;

import com.healthfit.user.dto.GoalRequest;
import com.healthfit.user.entity.Goal;
import com.healthfit.user.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;

    /**
     * Create a new goal
     */
    @Transactional
    public Goal createGoal(Long userId, GoalRequest request) {
        log.info("Creating goal for userId: {}", userId);

        Goal goal = Goal.builder()
                .userId(userId)
                .goalType(request.getGoalType())
                .title(request.getTitle())
                .description(request.getDescription())
                .targetValue(request.getTargetValue())
                .currentValue(request.getCurrentValue() != null ? request.getCurrentValue() : 0.0)
                .unit(request.getUnit())
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .targetDate(request.getTargetDate())
                .status(Goal.GoalStatus.NOT_STARTED)
                .build();

        // Status will be auto-updated by @PrePersist hook
        Goal savedGoal = goalRepository.save(goal);
        log.info("Goal created successfully with id: {}", savedGoal.getId());

        return savedGoal;
    }

    /**
     * Update an existing goal
     */
    @Transactional
    public Goal updateGoal(Long userId, Long goalId, GoalRequest request) {
        log.info("Updating goal id: {} for userId: {}", goalId, userId);

        Goal goal = getGoalById(goalId);
        if (!goal.getUserId().equals(userId)) {
            throw new RuntimeException("Goal does not belong to user");
        }

        if (request.getGoalType() != null) {
            goal.setGoalType(request.getGoalType());
        }
        if (request.getTitle() != null) {
            goal.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }
        if (request.getTargetValue() != null) {
            goal.setTargetValue(request.getTargetValue());
        }
        if (request.getCurrentValue() != null) {
            goal.setCurrentValue(request.getCurrentValue());
        }
        if (request.getUnit() != null) {
            goal.setUnit(request.getUnit());
        }
        if (request.getStartDate() != null) {
            goal.setStartDate(request.getStartDate());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        // Status will be auto-updated by @PreUpdate hook
        Goal updatedGoal = goalRepository.save(goal);
        log.info("Goal updated successfully");

        return updatedGoal;
    }

    /**
     * Update goal progress (current value)
     */
    @Transactional
    public Goal updateProgress(Long userId, Long goalId, Double currentValue) {
        log.info("Updating progress for goal id: {} to value: {}", goalId, currentValue);

        Goal goal = getGoalById(goalId);
        if (!goal.getUserId().equals(userId)) {
            throw new RuntimeException("Goal does not belong to user");
        }

        goal.setCurrentValue(currentValue);

        // Status will be auto-updated by @PreUpdate hook
        Goal updatedGoal = goalRepository.save(goal);
        log.info("Goal progress updated to {}%", updatedGoal.getProgress());

        return updatedGoal;
    }

    /**
     * Get all goals for a user
     */
    public List<Goal> getGoals(Long userId) {
        log.info("Fetching all goals for userId: {}", userId);
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get goals by status
     */
    public List<Goal> getGoalsByStatus(Long userId, Goal.GoalStatus status) {
        log.info("Fetching goals with status {} for userId: {}", status, userId);
        return goalRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Get goals by type
     */
    public List<Goal> getGoalsByType(Long userId, Goal.GoalType type) {
        log.info("Fetching goals with type {} for userId: {}", type, userId);
        return goalRepository.findByUserIdAndGoalType(userId, type);
    }

    /**
     * Get goal by ID
     */
    public Goal getGoalById(Long goalId) {
        log.info("Fetching goal by id: {}", goalId);
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));
    }

    /**
     * Delete a goal
     */
    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        log.info("Deleting goal id: {} for userId: {}", goalId, userId);

        Goal goal = getGoalById(goalId);
        if (!goal.getUserId().equals(userId)) {
            throw new RuntimeException("Goal does not belong to user");
        }

        goalRepository.delete(goal);
        log.info("Goal deleted successfully");
    }

    /**
     * Cancel a goal
     */
    @Transactional
    public Goal cancelGoal(Long userId, Long goalId) {
        log.info("Cancelling goal id: {} for userId: {}", goalId, userId);

        Goal goal = getGoalById(goalId);
        if (!goal.getUserId().equals(userId)) {
            throw new RuntimeException("Goal does not belong to user");
        }

        goal.setStatus(Goal.GoalStatus.CANCELLED);
        Goal cancelledGoal = goalRepository.save(goal);
        log.info("Goal cancelled successfully");

        return cancelledGoal;
    }
}
