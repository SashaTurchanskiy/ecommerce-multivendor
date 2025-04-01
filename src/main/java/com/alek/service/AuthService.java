package com.alek.service;

import com.alek.domain.USER_ROLE;
import com.alek.exception.SellerNotFoundException;
import com.alek.exception.UserNotFoundException;
import com.alek.exception.WrongOtpException;
import com.alek.request.LoginRequest;
import com.alek.response.AuthResponse;
import com.alek.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(String email, USER_ROLE role) throws Exception, UserNotFoundException, SellerNotFoundException;
    String createUser(SignupRequest req) throws Exception, WrongOtpException;
    AuthResponse signing(LoginRequest req) throws Exception, WrongOtpException;
}
