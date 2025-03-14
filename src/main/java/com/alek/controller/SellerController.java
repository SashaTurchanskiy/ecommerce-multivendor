package com.alek.controller;

import com.alek.domain.AccountStatus;
import com.alek.exception.SellerException;
import com.alek.model.Seller;
import com.alek.model.SellerReport;
import com.alek.model.VerificationCode;
import com.alek.repository.VerificationCodeRepo;
import com.alek.request.LoginRequest;
import com.alek.response.ApiResponse;
import com.alek.response.AuthResponse;
import com.alek.service.AuthService;
import com.alek.service.EmailService;
import com.alek.service.SellerService;
import com.alek.utils.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;
    private final VerificationCodeRepo verificationCodeRepo;
    private final AuthService authService;
    private final EmailService emailService;

    public SellerController(SellerService sellerService, VerificationCodeRepo verificationCodeRepo, AuthService authService, EmailService emailService) {
        this.sellerService = sellerService;
        this.verificationCodeRepo = verificationCodeRepo;
        this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginSeller(
            @RequestBody LoginRequest req
            ) throws Exception {

        String otp = req.getOtp();
        String email = req.getEmail();

        req.setEmail("seller_" + email);
        AuthResponse authResponse = authService.signing(req);


        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp)
    throws  Exception {

        VerificationCode verificationCode = verificationCodeRepo.findByOtp(otp);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)){
            throw new Exception("Invalid OTP");
        }

        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);

        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws Exception, MessagingException {
        Seller savedSeller = sellerService.createSeller(seller);

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(seller.getEmail());
        verificationCodeRepo.save(verificationCode);

        String subject = "Email Verification Code";
        String text = "Welcome to our platform. Veify your email using this link: ";
        String frontEnd_url = "http://localhost:3000/verify-seller/";
        emailService.sendVerificationOtpEmail(seller.getEmail(), verificationCode.getOtp(), subject, text + frontEnd_url);

        return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws SellerException {
        Seller seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerProfile(
            @RequestHeader("Authorization") String jwt) throws Exception {

        Seller seller = sellerService.getSellerProfile(jwt);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

//    @GetMapping("/report")
//    public ResponseEntity<SellerReport> getSellerReport(
//            @RequestHeader("Authorization") String jwt) throws Exception {
//    {
//     String email =  jwtProvider.getEmailFromJwtToken(jwt);
//     Seller seller = sellerService.getSellerByEmail(email);
//     SellerReport report = sellerReportService.getSellerReport(seller);;
//        return new ResponseEntity<>(report, HttpStatus.OK);
//    }

    @GetMapping()
    public ResponseEntity<List<Seller>> getAllSellers(
            @RequestParam(required = false) AccountStatus status) {
        List<Seller> sellers = sellerService.getAllSellers(status);
        return ResponseEntity.ok(sellers);
    }

    @PatchMapping()
    public ResponseEntity<Seller> updateSeller(
            @RequestHeader ("Authorization") String jwt,
            @RequestBody Seller seller) throws Exception {

        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatedSeller);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) throws Exception {

        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
