package com.soa.student.repository;

import com.soa.student.entity.Student;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByStudentId(String studentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Student s where s.studentId = :studentId")
    Optional<Student> findByStudentIdForUpdate(@Param("studentId") String studentId);
}
