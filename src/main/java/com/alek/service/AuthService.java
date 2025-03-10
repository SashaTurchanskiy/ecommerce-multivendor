package com.alek.service;

import com.alek.request.LoginRequest;
import com.alek.response.AuthResponse;
import com.alek.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(String email) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req);
}
