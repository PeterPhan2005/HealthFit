package com.soa.student.service;

import com.soa.student.entity.Student;
import com.soa.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student findStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId).orElse(null);
    }

    public Optional<Student> findByStudentIdForUpdate(String studentId) {
        return studentRepository.findByStudentIdForUpdate(studentId);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public void save(Student student) {
        studentRepository.save(student);
    }
}
