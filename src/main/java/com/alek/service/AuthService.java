package com.alek.service;

import com.alek.response.SignupRequest;

public interface AuthService {

    String createUser(SignupRequest req);
}
