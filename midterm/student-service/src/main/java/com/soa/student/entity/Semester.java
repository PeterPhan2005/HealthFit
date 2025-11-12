package com.soa.student.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Semesters")
public class Semester {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "SemesterCode", nullable = false, unique = true, columnDefinition = "VARCHAR(30)")
    private String semesterCode; // VD: HK1/2024-2025
    
    @Column(name = "SemesterName", nullable = false, columnDefinition = "VARCHAR(100)")
    private String semesterName; // VD: Học kỳ 1 năm học 2024-2025
    
    @Column(name = "Year", nullable = false, columnDefinition = "VARCHAR(20)")
    private String year; // VD: 2024-2025
    
    @Column(name = "SemesterOrder", nullable = false)
    private Integer semesterOrder; // VD: 1, 2, 3, 4 để sắp xếp thứ tự
    
    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true;
    
    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL)
    private List<StudentSemesterFee> studentSemesterFees;
    
    public Semester() {
    }
    
    public Semester(String semesterCode, String semesterName, String year, boolean isActive) {
        this.semesterCode = semesterCode;
        this.semesterName = semesterName;
        this.year = year;
        this.isActive = isActive;
    }
    
    public Semester(String semesterCode, String semesterName, String year, Integer semesterOrder, boolean isActive) {
        this.semesterCode = semesterCode;
        this.semesterName = semesterName;
        this.year = year;
        this.semesterOrder = semesterOrder;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSemesterCode() {
        return semesterCode;
    }
    
    public void setSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
    }
    
    public String getSemesterName() {
        return semesterName;
    }
    
    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Integer getSemesterOrder() {
        return semesterOrder;
    }
    
    public void setSemesterOrder(Integer semesterOrder) {
        this.semesterOrder = semesterOrder;
    }
    
    public List<StudentSemesterFee> getStudentSemesterFees() {
        return studentSemesterFees;
    }
    
    public void setStudentSemesterFees(List<StudentSemesterFee> studentSemesterFees) {
        this.studentSemesterFees = studentSemesterFees;
    }
    
    @Override
    public String toString() {
        return "Semester{" +
                "id=" + id +
                ", semesterCode='" + semesterCode + '\'' +
                ", semesterName='" + semesterName + '\'' +
                ", year='" + year + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
