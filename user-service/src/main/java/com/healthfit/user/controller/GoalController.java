package com.healthfit.user.controller;

import com.healthfit.common.dto.ApiResponse;
import com.healthfit.user.dto.GoalRequest;
import com.healthfit.user.entity.Goal;
import com.healthfit.user.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/goals")
@RequiredArgsConstructor
@Slf4j
public class GoalController {

    private final GoalService goalService;

    /**
     * Create a new goal
     * POST /api/users/{userId}/goals
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Goal>> createGoal(
            @PathVariable Long userId,
            @Valid @RequestBody GoalRequest request) {
        log.info("Creating goal for userId: {}", userId);
        
        Goal goal = goalService.createGoal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Goal created successfully", goal));
    }

    /**
     * Get all goals for a user
     * GET /api/users/{userId}/goals
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Goal>>> getGoals(@PathVariable Long userId) {
        log.info("Fetching all goals for userId: {}", userId);
        
        List<Goal> goals = goalService.getGoals(userId);
        return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goals));
    }

    /**
     * Get goals by status
     * GET /api/users/{userId}/goals?status=IN_PROGRESS
     */
    @GetMapping(params = "status")
    public ResponseEntity<ApiResponse<List<Goal>>> getGoalsByStatus(
            @PathVariable Long userId,
            @RequestParam Goal.GoalStatus status) {
        log.info("Fetching goals with status {} for userId: {}", status, userId);
        
        List<Goal> goals = goalService.getGoalsByStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goals));
    }

    /**
     * Get goals by type
     * GET /api/users/{userId}/goals?type=WEIGHT_LOSS
     */
    @GetMapping(params = "type")
    public ResponseEntity<ApiResponse<List<Goal>>> getGoalsByType(
            @PathVariable Long userId,
            @RequestParam Goal.GoalType type) {
        log.info("Fetching goals with type {} for userId: {}", type, userId);
        
        List<Goal> goals = goalService.getGoalsByType(userId, type);
        return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goals));
    }

    /**
     * Get goal by ID
     * GET /api/users/{userId}/goals/{goalId}
     */
    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Goal>> getGoalById(
            @PathVariable Long userId,
            @PathVariable Long goalId) {
        log.info("Fetching goal id: {} for userId: {}", goalId, userId);
        
        Goal goal = goalService.getGoalById(goalId);
        if (!goal.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Goal does not belong to user"));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Goal found", goal));
    }

    /**
     * Update a goal
     * PUT /api/users/{userId}/goals/{goalId}
     */
    @PutMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Goal>> updateGoal(
            @PathVariable Long userId,
            @PathVariable Long goalId,
            @Valid @RequestBody GoalRequest request) {
        log.info("Updating goal id: {} for userId: {}", goalId, userId);
        
        Goal updatedGoal = goalService.updateGoal(userId, goalId, request);
        return ResponseEntity.ok(ApiResponse.success("Goal updated successfully", updatedGoal));
    }

    /**
     * Update goal progress
     * PATCH /api/users/{userId}/goals/{goalId}/progress
     */
    @PatchMapping("/{goalId}/progress")
    public ResponseEntity<ApiResponse<Goal>> updateProgress(
            @PathVariable Long userId,
            @PathVariable Long goalId,
            @RequestBody Map<String, Double> payload) {
        log.info("Updating progress for goal id: {}", goalId);
        
        Double currentValue = payload.get("currentValue");
        if (currentValue == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("currentValue is required"));
        }
        
        Goal updatedGoal = goalService.updateProgress(userId, goalId, currentValue);
        return ResponseEntity.ok(ApiResponse.success("Progress updated successfully", updatedGoal));
    }

    /**
     * Cancel a goal
     * POST /api/users/{userId}/goals/{goalId}/cancel
     */
    @PostMapping("/{goalId}/cancel")
    public ResponseEntity<ApiResponse<Goal>> cancelGoal(
            @PathVariable Long userId,
            @PathVariable Long goalId) {
        log.info("Cancelling goal id: {} for userId: {}", goalId, userId);
        
        Goal cancelledGoal = goalService.cancelGoal(userId, goalId);
        return ResponseEntity.ok(ApiResponse.success("Goal cancelled successfully", cancelledGoal));
    }

    /**
     * Delete a goal
     * DELETE /api/users/{userId}/goals/{goalId}
     */
    @DeleteMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @PathVariable Long userId,
            @PathVariable Long goalId) {
        log.info("Deleting goal id: {} for userId: {}", goalId, userId);
        
        goalService.deleteGoal(userId, goalId);
        return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully", null));
    }
}
