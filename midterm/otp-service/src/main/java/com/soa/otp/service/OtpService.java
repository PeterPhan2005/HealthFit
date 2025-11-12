package com.soa.otp.service;

import com.soa.otp.entity.Otp;
import com.soa.otp.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private static final SecureRandom random = new SecureRandom();

    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.validity.minutes:5}")
    private int otpValidityMinutes;

    @Autowired
    private OtpRepository otpRepository;

    /**
     * Generate OTP code for payment transaction
     * @param customerId Customer ID making the payment
     * @return 6-digit OTP code
     */
    @Transactional
    public String generateOtp(Long customerId) {
        // Invalidate all previous OTPs for this customer to prevent multiple valid OTPs
        otpRepository.invalidateAllCustomerOtps(customerId);
        
        String code = String.format("%0" + otpLength + "d", random.nextInt((int) Math.pow(10, otpLength)));
        Otp otp = new Otp(customerId, code);
        otpRepository.save(otp);
        return code;
    }

    /**
     * Verify OTP code and mark as used
     * @param customerId Customer ID
     * @param code OTP code to verify
     * @return true if valid and not expired, false otherwise
     */
    @Transactional
    public boolean verifyAndConsumeOtp(Long customerId, String code) {
        LocalDateTime validTime = LocalDateTime.now().minusMinutes(otpValidityMinutes);
        Optional<Otp> otpOpt = otpRepository.findValidOtp(customerId, code, validTime);
        
        if (otpOpt.isPresent()) {
            Otp otp = otpOpt.get();
            otpRepository.markAsUsed(otp.getId());
            return true;
        }
        return false;
    }

    /**
     * Scheduled task to clean up expired OTPs
     * Runs every 5 minutes (300000 ms)
     */
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void cleanupExpiredOtps() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(otpValidityMinutes);
        otpRepository.deleteExpiredOtps(expireTime);
        System.out.println("Cleaned up expired OTPs before: " + expireTime);
    }
}
