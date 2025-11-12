package com.soa.student.controller;

import com.soa.student.entity.Student;
import com.soa.student.entity.StudentSemesterFee;
import com.soa.student.service.StudentSemesterFeeService;
import com.soa.student.service.StudentService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Student operations.
 * Handles student search, semester fees management.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private StudentSemesterFeeService studentSemesterFeeService;

    /**
     * Search student by student ID.
     * 
     * @param studentId Student ID to search
     * @return Student information
     */
    @GetMapping("/search/{studentId}")
    public ResponseEntity<?> searchStudent(@PathVariable String studentId) {
        try {
            Student student = studentService.findStudentByStudentId(studentId);
            if (student == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Student not found"
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("studentId", student.getStudentId());
            response.put("fullName", student.getFullName());
            response.put("email", student.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            ));
        }
    }

    /**
     * Get student by ID (for internal service calls).
     * 
     * @param studentId Student ID
     * @return Student data
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudent(@PathVariable String studentId) {
        try {
            Student student = studentService.findStudentByStudentId(studentId);
            if (student == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Student not found"
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("studentId", student.getStudentId());
            response.put("fullName", student.getFullName());
            response.put("email", student.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            ));
        }
    }

    /**
     * Get all semesters with fees for a student.
     * 
     * @param studentId Student ID
     * @return List of semesters with payment status
     */
    @GetMapping("/{studentId}/semesters")
    public ResponseEntity<?> getStudentSemesters(@PathVariable String studentId) {
        try {
            Student student = studentService.findStudentByStudentId(studentId);
            if (student == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Student not found"
                ));
            }

            List<StudentSemesterFee> fees = studentSemesterFeeService.getFeesByStudentId(studentId);
            
            if (fees == null || fees.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }

            // Sắp xếp theo semesterOrder
            fees.sort((a, b) -> Integer.compare(
                a.getSemester().getSemesterOrder(), 
                b.getSemester().getSemesterOrder()
            ));

            // Tính toán canPay: chỉ có thể thanh toán nếu tất cả học kỳ trước đã thanh toán
            boolean allPreviousPaid = true;
            
            List<Map<String, Object>> semesters = new java.util.ArrayList<>();
            for (StudentSemesterFee fee : fees) {
                Map<String, Object> semesterData = new HashMap<>();
                semesterData.put("id", fee.getId());
                semesterData.put("semesterCode", fee.getSemester().getSemesterCode());
                semesterData.put("semesterName", fee.getSemester().getSemesterName());
                semesterData.put("fee", fee.getFee());
                semesterData.put("paidAmount", fee.getPaidAmount());
                semesterData.put("remainingFee", fee.getRemainingFee());
                semesterData.put("isPaid", fee.isPaid());
                
                // Chỉ có thể thanh toán nếu chưa thanh toán VÀ tất cả học kỳ trước đã thanh toán
                boolean canPay = !fee.isPaid() && allPreviousPaid;
                semesterData.put("canPay", canPay);
                
                semesters.add(semesterData);
                
                // Cập nhật trạng thái cho học kỳ tiếp theo
                if (!fee.isPaid()) {
                    allPreviousPaid = false;
                }
            }

            return ResponseEntity.ok(semesters);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            ));
        }
    }

    /**
     * Get semester fee details (called by payment service to get semester info).
     * GET /api/students/{studentId}/semesters/{semesterFeeId}
     * 
     * @param studentId Student ID
     * @param semesterFeeId Semester Fee ID
     * @return Semester fee details
     */
    @GetMapping("/{studentId}/semesters/{semesterFeeId}")
    public ResponseEntity<?> getSemesterFee(
            @PathVariable String studentId,
            @PathVariable Long semesterFeeId) {
        try {
            StudentSemesterFee fee = studentSemesterFeeService.findById(semesterFeeId);
            
            if (fee == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Semester fee not found"
                ));
            }
            
            if (!fee.getStudent().getStudentId().equals(studentId)) {
                return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", "Student ID mismatch"
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                    "id", fee.getId(),
                    "fee", fee.getFee(),
                    "paidAmount", fee.getPaidAmount(),
                    "remainingFee", fee.getRemainingFee(),
                    "isPaid", fee.isPaid(),
                    "semesterCode", fee.getSemester().getSemesterCode(),
                    "semesterName", fee.getSemester().getSemesterName(),
                    "year", fee.getSemester().getYear()
                )
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Error retrieving semester fee: " + e.getMessage()
            ));
        }
    }

    /**
     * Mark semester fee as paid (called by payment service after successful payment).
     * PUT /api/students/{studentId}/semesters/{semesterFeeId}/mark-paid
     * Body: { "amount": 5000000 }
     * 
     * @param studentId Student ID
     * @param semesterFeeId Semester Fee ID
     * @param request Payment amount
     * @return Success response
     */
    @PutMapping("/{studentId}/semesters/{semesterFeeId}/mark-paid")
    public ResponseEntity<?> markSemesterFeePaid(
            @PathVariable String studentId,
            @PathVariable Long semesterFeeId,
            @RequestBody Map<String, Object> request) {
        try {
            StudentSemesterFee fee = studentSemesterFeeService.findById(semesterFeeId);
            
            if (fee == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Semester fee not found"
                ));
            }
            
            if (!fee.getStudent().getStudentId().equals(studentId)) {
                return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", "Student ID mismatch"
                ));
            }
            
            if (fee.isPaid()) {
                return ResponseEntity.status(409).body(Map.of(
                    "success", false,
                    "errorType", "ALREADY_PAID",
                    "message", "Học phí này đã được thanh toán rồi",
                    "shouldRefresh", true
                ));
            }
            
            // Get payment amount
            Double amount = request.get("amount") != null 
                ? Double.valueOf(request.get("amount").toString()) 
                : fee.getRemainingFee();
            
            // Mark as paid
            fee.setPaidAmount(fee.getPaidAmount() + amount);
            
            // Remaining fee will be calculated by entity getter
            if (fee.getPaidAmount() >= fee.getFee()) {
                fee.setPaid(true);
            }
            
            studentSemesterFeeService.save(fee);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Semester fee marked as paid",
                "isPaid", fee.isPaid(),
                "paidAmount", fee.getPaidAmount(),
                "remainingFee", fee.getRemainingFee()
            ));
            
        } catch (OptimisticLockException e) {
            // Concurrent update detected - another transaction modified this record
            return ResponseEntity.status(409).body(Map.of(
                "success", false,
                "errorType", "CONCURRENT_UPDATE",
                "message", "Học phí đang được xử lý bởi giao dịch khác. Vui lòng thử lại sau.",
                "shouldRefresh", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to mark as paid: " + e.getMessage()
            ));
        }
    }
}
