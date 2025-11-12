package com.soa.payment.service;

import com.soa.payment.client.CustomerClient;
import com.soa.payment.client.StudentClient;
import com.soa.payment.dto.CustomerDTO;
import com.soa.payment.dto.PaymentEvent;
import com.soa.payment.dto.StudentDTO;
import com.soa.payment.entity.Transaction;
import com.soa.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final TransactionRepository transactionRepository;
    private final CustomerClient customerClient;
    private final StudentClient studentClient;
    private final RabbitTemplate rabbitTemplate;
    
    @Transactional
    public Transaction processSimplePayment(Long customerId, String studentId, BigDecimal amount) {
        CustomerDTO customer = customerClient.getCustomer(customerId);
        if (customer.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        StudentDTO student = studentClient.getStudent(studentId);
        
        Map<String, Object> deductRequest = new HashMap<>();
        deductRequest.put("amount", amount);
        customerClient.deductBalance(customerId, deductRequest);
        
        Transaction transaction = new Transaction();
        transaction.setCustomerId(customerId);
        transaction.setAmount(amount);
        transaction.setTransactionType("SIMPLE_PAYMENT");
        transaction.setStatus("COMPLETED");
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Payment processed: Transaction ID {}", savedTransaction.getId());
        
        publishPaymentEvent(savedTransaction, customer, student);
        
        return savedTransaction;
    }
    
    @Transactional
    public Transaction processPaymentWithSemesterFee(Long customerId, String studentId, BigDecimal amount, Long semesterFeeId) {
        // Step 1: Get customer and student info
        CustomerDTO customer = customerClient.getCustomer(customerId);
        StudentDTO student = studentClient.getStudent(studentId);
        
        // Get semester info for email notification
        String semesterCode = "N/A";
        if (semesterFeeId != null) {
            try {
                Map<String, Object> semesterResponse = studentClient.getSemesterFee(studentId, semesterFeeId);
                if (semesterResponse != null && semesterResponse.get("data") != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> semesterData = (Map<String, Object>) semesterResponse.get("data");
                    semesterCode = (String) semesterData.get("semesterCode");
                }
            } catch (Exception e) {
                log.error("Failed to get semester info: {}", e.getMessage());
            }
        }
        
        // Step 2: CRITICAL - Mark semester fee as paid FIRST to trigger optimistic lock check
        // This prevents duplicate payments via version check in student-service
        if (semesterFeeId != null) {
            Map<String, Object> markPaidRequest = new HashMap<>();
            markPaidRequest.put("amount", amount.doubleValue());
            studentClient.markSemesterFeePaid(studentId, semesterFeeId, markPaidRequest);
            log.info("Semester fee {} marked as paid for student {}", semesterFeeId, studentId);
        }
        
        // Step 3: Deduct customer balance AFTER successful fee marking
        // Customer service will check balance again at this point (prevents race condition)
        Map<String, Object> deductRequest = new HashMap<>();
        deductRequest.put("amount", amount);
        customerClient.deductBalance(customerId, deductRequest);
        log.info("Balance deducted for customer {}: {}", customerId, amount);
        
        Transaction transaction = new Transaction();
        transaction.setCustomerId(customerId);
        transaction.setAmount(amount);
        transaction.setSemesterId(semesterFeeId);
        transaction.setTransactionType("TUITION_PAYMENT");
        transaction.setStatus("COMPLETED");
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Payment with semester fee processed: Transaction ID {}", savedTransaction.getId());
        
        publishPaymentEvent(savedTransaction, customer, student, semesterCode);
        
        return savedTransaction;
    }
    
    public List<Transaction> getCustomerTransactions(Long customerId) {
        return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
    
    // Overloaded method for backward compatibility (simple payments without semester)
    private void publishPaymentEvent(Transaction transaction, CustomerDTO customer, StudentDTO student) {
        publishPaymentEvent(transaction, customer, student, "N/A");
    }
    
    // Main method with semester code
    private void publishPaymentEvent(Transaction transaction, CustomerDTO customer, StudentDTO student, String semesterCode) {
        PaymentEvent event = new PaymentEvent();
        event.setTransactionId(transaction.getId());
        event.setCustomerId(customer.getId());
        event.setCustomerName(customer.getFullName());
        event.setCustomerEmail(customer.getEmail());
        event.setStudentId(student.getId());
        event.setStudentName(student.getFullName());
        event.setAmount(transaction.getAmount());
        event.setTransactionType(transaction.getTransactionType());
        event.setStatus(transaction.getStatus());
        event.setSemesterCode(semesterCode);
        event.setCreatedAt(transaction.getCreatedAt());
        
        rabbitTemplate.convertAndSend("payment.exchange", "payment.completed", event);
        log.info("Payment event published to RabbitMQ: Transaction ID {}, Semester: {}", transaction.getId(), semesterCode);
    }
}
