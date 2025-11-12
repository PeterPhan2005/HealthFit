package com.soa.student.repository;

import com.soa.student.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    
    Optional<Semester> findBySemesterCode(String semesterCode);
    
    List<Semester> findByIsActiveTrue();
    
    List<Semester> findByYearOrderBySemesterCodeAsc(String year);
}
