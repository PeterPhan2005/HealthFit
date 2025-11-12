package com.soa.student.service;

import com.soa.student.entity.Semester;
import com.soa.student.entity.Student;
import com.soa.student.entity.StudentSemesterFee;
import com.soa.student.repository.StudentSemesterFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentSemesterFeeService {
    
    @Autowired
    private StudentSemesterFeeRepository studentSemesterFeeRepository;
    
    public List<StudentSemesterFee> getAllFeesForStudent(Student student) {
        return studentSemesterFeeRepository.findByStudent(student);
    }
    
    public List<StudentSemesterFee> getUnpaidFeesForStudent(Student student) {
        return studentSemesterFeeRepository.findByStudentAndIsPaidFalse(student);
    }
    
    public Optional<StudentSemesterFee> getFeeByStudentAndSemester(Student student, Semester semester) {
        return studentSemesterFeeRepository.findByStudentAndSemester(student, semester);
    }
    
    public Optional<StudentSemesterFee> getFeeById(Long id) {
        return studentSemesterFeeRepository.findById(id);
    }
    
    public StudentSemesterFee findById(Long id) {
        return studentSemesterFeeRepository.findById(id).orElse(null);
    }
    
    public Optional<StudentSemesterFee> getFeeByIdWithLock(Long id) {
        return studentSemesterFeeRepository.findByIdWithLock(id);
    }
    
    public List<StudentSemesterFee> getFeesByStudentId(String studentId) {
        return studentSemesterFeeRepository.findByStudentId(studentId);
    }
    
    @Transactional
    public StudentSemesterFee save(StudentSemesterFee studentSemesterFee) {
        return studentSemesterFeeRepository.save(studentSemesterFee);
    }
    
    @Transactional
    public void updatePayment(StudentSemesterFee fee, double amount) {
        fee.setPaidAmount(fee.getPaidAmount() + amount);
        if (fee.getPaidAmount() >= fee.getFee()) {
            fee.setPaid(true);
        }
        studentSemesterFeeRepository.save(fee);
    }
}
