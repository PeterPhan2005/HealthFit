package com.soa.student.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "StudentSemesterFees", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"studentId", "semesterId"}))
public class StudentSemesterFee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semesterId", nullable = false)
    private Semester semester;
    
    @Column(name = "Fee", nullable = false)
    private double fee;
    
    @Column(name = "PaidAmount", nullable = false)
    private double paidAmount = 0.0;
    
    @Column(name = "IsPaid", nullable = false)
    private boolean isPaid = false;
    
    @Version
    @Column(name = "Version")
    private Long version; // Optimistic locking
    
    public StudentSemesterFee() {
    }
    
    public StudentSemesterFee(Student student, Semester semester, double fee) {
        this.student = student;
        this.semester = semester;
        this.fee = fee;
        this.paidAmount = 0.0;
        this.isPaid = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Semester getSemester() {
        return semester;
    }
    
    public void setSemester(Semester semester) {
        this.semester = semester;
    }
    
    public double getFee() {
        return fee;
    }
    
    public void setFee(double fee) {
        this.fee = fee;
    }
    
    public double getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
    
    public void setPaid(boolean paid) {
        isPaid = paid;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    public double getRemainingFee() {
        return fee - paidAmount;
    }
    
    @Override
    public String toString() {
        return "StudentSemesterFee{" +
                "id=" + id +
                ", student=" + (student != null ? student.getStudentId() : "null") +
                ", semester=" + (semester != null ? semester.getSemesterCode() : "null") +
                ", fee=" + fee +
                ", paidAmount=" + paidAmount +
                ", isPaid=" + isPaid +
                '}';
    }
}
