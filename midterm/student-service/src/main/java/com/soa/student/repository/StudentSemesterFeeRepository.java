package com.soa.student.repository;

import com.soa.student.entity.Student;
import com.soa.student.entity.Semester;
import com.soa.student.entity.StudentSemesterFee;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSemesterFeeRepository extends JpaRepository<StudentSemesterFee, Long> {
    
    Optional<StudentSemesterFee> findByStudentAndSemester(Student student, Semester semester);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ssf FROM StudentSemesterFee ssf WHERE ssf.id = :id")
    Optional<StudentSemesterFee> findByIdWithLock(@Param("id") Long id);
    
    List<StudentSemesterFee> findByStudent(Student student);
    
    List<StudentSemesterFee> findByStudentAndIsPaidFalse(Student student);
    
    @Query("SELECT ssf FROM StudentSemesterFee ssf WHERE ssf.student.studentId = :studentId")
    List<StudentSemesterFee> findByStudentId(@Param("studentId") String studentId);
}
