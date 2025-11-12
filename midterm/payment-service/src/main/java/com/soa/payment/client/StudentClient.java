package com.soa.payment.client;

import com.soa.payment.dto.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "student-service", url = "${student-service.url:http://localhost:8083}")      
public interface StudentClient {

    @GetMapping("/api/students/{id}")
    StudentDTO getStudent(@PathVariable("id") String id);
    
    @GetMapping("/api/students/{studentId}/semesters/{semesterFeeId}")
    Map<String, Object> getSemesterFee(
        @PathVariable("studentId") String studentId,
        @PathVariable("semesterFeeId") Long semesterFeeId
    );
    
    @PutMapping("/api/students/{studentId}/semesters/{semesterFeeId}/mark-paid")
    Map<String, Object> markSemesterFeePaid(
        @PathVariable("studentId") String studentId,
        @PathVariable("semesterFeeId") Long semesterFeeId,
        @RequestBody Map<String, Object> request
    );
}