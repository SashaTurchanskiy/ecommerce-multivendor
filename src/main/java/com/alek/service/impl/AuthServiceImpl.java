package com.alek.service.impl;

import com.alek.config.JwtProvider;
import com.alek.domain.USER_ROLE;
import com.alek.exception.SellerNotFoundException;
import com.alek.exception.UserNotFoundException;
import com.alek.exception.WrongOtpException;
import com.alek.model.Cart;
import com.alek.model.Seller;
import com.alek.model.User;
import com.alek.model.VerificationCode;
import com.alek.repository.CartRepo;
import com.alek.repository.SellerRepo;
import com.alek.repository.UserRepo;
import com.alek.repository.VerificationCodeRepo;
import com.alek.request.LoginRequest;
import com.alek.response.AuthResponse;
import com.alek.response.SignupRequest;
import com.alek.service.AuthService;
import com.alek.service.EmailService;
import com.alek.utils.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService  {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final CartRepo cartRepo;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepo verificationCodeRepo;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserService;
    private final SellerRepo sellerRepo;

    public AuthServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, CartRepo cartRepo, JwtProvider jwtProvider, VerificationCodeRepo verificationCodeRepo, EmailService emailService, CustomUserServiceImpl customUserService, SellerRepo sellerRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.cartRepo = cartRepo;
        this.jwtProvider = jwtProvider;
        this.verificationCodeRepo = verificationCodeRepo;

        this.emailService = emailService;
        this.customUserService = customUserService;
        this.sellerRepo = sellerRepo;
    }

    @Override
    public void sentLoginOtp(String email, USER_ROLE role) throws Exception,UserNotFoundException, SellerNotFoundException  {
        String SIGNING_PREFIX = "signing_";


        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());

            if (role.equals(USER_ROLE.ROLE_SELLER)) {
                Seller seller = sellerRepo.findByEmail(email);
                if (seller == null) {
                    throw new SellerNotFoundException("Seller not found with provided email");
                }
            } else {
                User user = userRepo.findByEmail(email);
                if (user == null) {
                    throw new UserNotFoundException("User not exist with provided email");
                }
            }
        }

            VerificationCode isExist = verificationCodeRepo.findByEmail(email);
            if (isExist != null) {
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
    public String createUser(SignupRequest req) throws  WrongOtpException {

        VerificationCode verificationCode = verificationCodeRepo.findByEmail((req.getEmail()));
        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())){
            throw new WrongOtpException("wrong otp");
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

    @Override
    public AuthResponse signing(LoginRequest req) throws  WrongOtpException {
        String username = req.getEmail();
        String otp = req.getOtp();
        
        Authentication authentication = authentication(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("login successfully");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty()? null: authorities.iterator().next().getAuthority();
        authResponse.setRole(USER_ROLE.valueOf(roleName));
        
        return authResponse;
    }

    private Authentication authentication(String username, String otp) throws WrongOtpException {
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        String SELLER_PREFIX = "seller_";

        if (username.startsWith(SELLER_PREFIX)){
            username = username.substring(SELLER_PREFIX.length());
        }

        if (userDetails == null){
            throw new BadCredentialsException("invalid username");
        }

        VerificationCode verificationCode = verificationCodeRepo.findByEmail(username);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)){
            throw new WrongOtpException("invalid otp");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
