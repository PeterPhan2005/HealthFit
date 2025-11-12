package com.soa.auth.service;

import com.soa.auth.security.request.LoginRequest;
import com.soa.auth.security.response.UserInfoResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    
    UserInfoResponse login(LoginRequest loginRequest);
    
    UserInfoResponse getCurrentUserDetails(Authentication authentication);
}
