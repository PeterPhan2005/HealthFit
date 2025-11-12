package com.soa.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "otp-service", url = "${otp.service.url:http://localhost:8085}")
public interface OtpClient {
    
    @PostMapping("/api/otp/generate")
    Map<String, Object> generateOtp(@RequestBody Map<String, String> request);
    
    @PostMapping("/api/otp/verify")
    Map<String, Object> verifyOtp(@RequestBody Map<String, String> request);
}
