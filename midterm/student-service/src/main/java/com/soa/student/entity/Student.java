package com.soa.student.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Students")
public class Student {

    @Id
    @Column(name = "StudentId", nullable = false, columnDefinition = "VARCHAR(20)")
    private String studentId;

    @Column(name = "FullName", nullable = false, columnDefinition = "VARCHAR(50)")
    private String fullName;

    @Column(name = "Email", nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
    private String email;

    @Version
    @Column(name = "Version")
    private Long version; // Optimistic locking for concurrency control

    public Student() {
    }

    public Student(String studentId, String fullName, String email) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
