package com.alek.service.impl;

import com.alek.config.JwtProvider;
import com.alek.model.User;
import com.alek.repository.UserRepo;
import com.alek.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final JwtProvider jwtProvider;

    public UserServiceImpl(UserRepo userRepo, JwtProvider jwtProvider) {
        this.userRepo = userRepo;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);

        return this.findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepo.findByEmail(email);
        if (user == null){
            throw new Exception("User not found " + email);
        }

        return user;
    }
}
