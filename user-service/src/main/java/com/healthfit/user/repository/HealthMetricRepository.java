package com.healthfit.user.repository;

import com.healthfit.user.entity.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {
    
    List<HealthMetric> findByUserIdOrderByRecordedDateDesc(Long userId);
    
    List<HealthMetric> findByUserIdAndRecordedDateBetweenOrderByRecordedDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);
    
    Optional<HealthMetric> findTopByUserIdOrderByRecordedDateDesc(Long userId);
}
