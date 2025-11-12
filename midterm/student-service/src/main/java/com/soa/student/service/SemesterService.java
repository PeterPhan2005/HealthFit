package com.soa.student.service;

import com.soa.student.entity.Semester;
import com.soa.student.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SemesterService {
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }
    
    public List<Semester> getActiveSemesters() {
        return semesterRepository.findByIsActiveTrue();
    }
    
    public Optional<Semester> getSemesterById(Long id) {
        return semesterRepository.findById(id);
    }
    
    public Optional<Semester> getSemesterByCode(String semesterCode) {
        return semesterRepository.findBySemesterCode(semesterCode);
    }
    
    public Semester save(Semester semester) {
        return semesterRepository.save(semester);
    }
}
