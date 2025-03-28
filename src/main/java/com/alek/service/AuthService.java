package com.alek.service;

import com.alek.domain.USER_ROLE;
import com.alek.request.LoginRequest;
import com.alek.response.AuthResponse;
import com.alek.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(String email, USER_ROLE role) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req) throws Exception;
}
