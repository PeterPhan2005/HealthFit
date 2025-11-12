package com.soa.otp.repository;

import com.soa.otp.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    @Query("SELECT o FROM Otp o WHERE o.customerId = :customerId " +
           "AND o.code = :code AND o.used = false AND o.createdAt > :validTime")
    Optional<Otp> findValidOtp(@Param("customerId") Long customerId,
                                @Param("code") String code,
                                @Param("validTime") LocalDateTime validTime);

    @Modifying
    @Query("UPDATE Otp o SET o.used = true WHERE o.id = :id")
    void markAsUsed(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Otp o SET o.used = true WHERE o.customerId = :customerId AND o.used = false")
    void invalidateAllCustomerOtps(@Param("customerId") Long customerId);

    @Modifying
    @Query("DELETE FROM Otp o WHERE o.createdAt < :expireTime")
    void deleteExpiredOtps(@Param("expireTime") LocalDateTime expireTime);
}
