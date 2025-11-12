package com.soa.payment.client;

import com.soa.payment.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "customer-service", url = "${customer-service.url:http://localhost:8082}")
public interface CustomerClient {
    
    @GetMapping("/api/customers/{id}")
    CustomerDTO getCustomer(@PathVariable("id") Long id);
    
    @PutMapping("/api/customers/{id}/deduct")
    void deductBalance(@PathVariable("id") Long id, @RequestBody Map<String, Object> request);
}
