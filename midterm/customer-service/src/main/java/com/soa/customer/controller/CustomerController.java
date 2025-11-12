package com.soa.customer.controller;

import com.soa.customer.entity.Customer;
import com.soa.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Customer operations.
 * Handles customer profile, balance management.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Get customer by ID (for inter-service communication).
     * 
     * @param id Customer ID
     * @return Customer data
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        try {
            var customerOpt = customerService.findByIdForUpdate(id);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Customer not found"
                ));
            }

            Customer customer = customerOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", customer.getId());
            response.put("username", customer.getUsername());
            response.put("fullName", customer.getFullName());
            response.put("email", customer.getEmail());
            response.put("phoneNumber", customer.getPhoneNumber());
            response.put("balance", customer.getBalance());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            ));
        }
    }

    /**
     * Deduct money from customer balance (for payment service).
     * This endpoint will be called by PAYMENT-SERVICE via OpenFeign.
     * 
     * @param id Customer ID
     * @param request Deduct request with amount
     * @return Success/failure status
     */
    @PutMapping("/{id}/deduct")
    public ResponseEntity<?> deductBalance(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        try {
            Double amount = ((Number) request.get("amount")).doubleValue();
            
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid amount"
                ));
            }

            var customerOpt = customerService.findByIdForUpdate(id);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Customer not found"
                ));
            }

            Customer customer = customerOpt.get();
            if (customer.getBalance() < amount) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "errorType", "INSUFFICIENT_BALANCE",
                    "message", "Số dư không đủ để thực hiện giao dịch",
                    "currentBalance", customer.getBalance(),
                    "requiredAmount", amount
                ));
            }

            customer.setBalance(customer.getBalance() - amount);
            customerService.save(customer);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Balance deducted successfully",
                "newBalance", customer.getBalance()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            ));
        }
    }
}
