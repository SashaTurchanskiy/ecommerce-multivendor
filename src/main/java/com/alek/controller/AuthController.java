package com.alek.controller;

import com.alek.domain.USER_ROLE;
import com.alek.model.User;
import com.alek.repository.UserRepo;
import com.alek.response.AuthResponse;
import com.alek.response.SignupRequest;
import com.alek.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")

public class AuthController {

    private final UserRepo userRepo;
    private final AuthService authService;

    public AuthController(UserRepo userRepo, AuthService authService) {
        this.userRepo = userRepo;
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignupRequest request){

        String jwt = authService.createUser(request);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register successfully");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);

        return  ResponseEntity.ok(res);
    }
}
