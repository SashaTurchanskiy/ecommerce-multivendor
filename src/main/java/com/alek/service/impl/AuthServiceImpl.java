package com.alek.service.impl;

import com.alek.config.JwtProvider;
import com.alek.domain.USER_ROLE;
import com.alek.model.Cart;
import com.alek.model.User;
import com.alek.model.VerificationCode;
import com.alek.repository.CartRepo;
import com.alek.repository.UserRepo;
import com.alek.repository.VerificationCodeRepo;
import com.alek.response.SignupRequest;
import com.alek.service.AuthService;
import com.alek.service.EmailService;
import com.alek.utils.OtpUtil;
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
public class AuthServiceImpl implements AuthService  {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final CartRepo cartRepo;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepo verificationCodeRepo;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, CartRepo cartRepo, JwtProvider jwtProvider, VerificationCodeRepo verificationCodeRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.cartRepo = cartRepo;
        this.jwtProvider = jwtProvider;
        this.verificationCodeRepo = verificationCodeRepo;

        this.emailService = emailService;
    }

    @Override
    public void sentLoginOtp(String email) throws Exception {
        String SIGNING_PREFIX = "signing_";

        if (email.startsWith(SIGNING_PREFIX)){
            email = email.substring(SIGNING_PREFIX.length());

            User user = userRepo.findByEmail(email);
            if (user == null){
                throw new Exception("User not exist with provided email");
            }
        }

        VerificationCode isExist = verificationCodeRepo.findByEmail(email);
        if (isExist != null){
            verificationCodeRepo.delete(isExist);
        }

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);

        verificationCodeRepo.save(verificationCode);

        String subject = "Login OTP";
        String text = "Your login/signup OTP is " + otp;

        emailService.sendVerificationOtpEmail(email, otp, subject, text);

    }

    @Override
    public String createUser(SignupRequest req) throws Exception {

        VerificationCode verificationCode = verificationCodeRepo.findByEmail((req.getEmail()));
        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())){
            throw new Exception("wrong otp");
        }

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
