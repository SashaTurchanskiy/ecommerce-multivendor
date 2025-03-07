package com.alek.service.impl;

import com.alek.config.JwtProvider;
import com.alek.domain.USER_ROLE;
import com.alek.model.Cart;
import com.alek.model.User;
import com.alek.repository.CartRepo;
import com.alek.repository.UserRepo;
import com.alek.response.SignupRequest;
import com.alek.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final CartRepo cartRepo;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, CartRepo cartRepo, JwtProvider jwtProvider) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.cartRepo = cartRepo;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public String createUser(SignupRequest req) {

        User user = userRepo.findByEmail(req.getEmail());

        if (user == null){
            User createdUser = new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setPhone("132145123");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));

            user = userRepo.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepo.save(cart);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(),null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        return jwtProvider.generateToken(authentication);
    }
}
