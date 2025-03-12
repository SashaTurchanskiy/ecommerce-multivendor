package com.alek.service.impl;

import com.alek.config.JwtProvider;
import com.alek.domain.AccountStatus;
import com.alek.model.Address;
import com.alek.model.Seller;
import com.alek.repository.AddressRepo;
import com.alek.repository.SellerRepo;
import com.alek.service.SellerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {

    private final SellerRepo sellerRepo;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepo addressRepo;


    public SellerServiceImpl(SellerRepo sellerRepo, JwtProvider jwtProvider, PasswordEncoder passwordEncoder, AddressRepo addressRepo) {
        this.sellerRepo = sellerRepo;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.addressRepo = addressRepo;
    }

    @Override
    public Seller getSellerProfile(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return this.getSellerByEmail(email);
    }

    @Override
    public Seller createSeller(Seller seller) throws Exception {
        Seller sellerExist = sellerRepo.findByEmail(seller.getEmail());
        if (sellerExist != null) {
            throw new Exception("Seller already exists with email: " + seller.getEmail());
        }
        Address savedAddress = addressRepo.save(seller.getPickupAddress());

        Seller newSeller = new Seller();
        newSeller.setEmail(seller.getEmail());
        newSeller.setPassword(passwordEncoder.encode(seller.getPassword()));
        newSeller.setSellerName(seller.getSellerName());
        newSeller.setPickupAddress(savedAddress);
        newSeller.setGSTIN(seller.getGSTIN());
        newSeller.setRole(seller.getRole());
        newSeller.setMobile(seller.getMobile());
        newSeller.setBankDetails(seller.getBankDetails());
        newSeller.setBusinessDetails(seller.getBusinessDetails());


        return sellerRepo.save(newSeller);
    }

    @Override
    public Seller getSellerById(Long id) throws Exception {
        return sellerRepo.findById(id).orElseThrow(()-> new Exception("Seller not found with id: " + id));
    }

    @Override
    public Seller getSellerByEmail(String email) throws Exception {
        Seller seller = sellerRepo.findByEmail(email);

        if (seller == null) {
            throw new Exception("Seller not found with email: " + email);
        }
        return seller;
    }

    @Override
    public List<Seller> getAllSellers(AccountStatus status) {
        return sellerRepo.findByAccountStatus(status);
    }

    @Override
    public Seller updateSeller(Long id, Seller seller) throws Exception {
        Seller existingSeller = this.getSellerById(id);

        if (seller.getSellerName() != null) {
            existingSeller.setSellerName(seller.getSellerName());
        }
        if (seller.getMobile() != null) {
            existingSeller.setMobile(seller.getMobile());
        }
        if (seller.getEmail() != null) {
            existingSeller.setEmail(seller.getEmail());
        }

        if (seller.getBusinessDetails() != null
                && seller.getBusinessDetails().getBusinessName() != null) {
        }
        {
            existingSeller.getBusinessDetails().setBusinessName(
                    seller.getBusinessDetails().getBusinessName()
            );
            if (seller.getBankDetails() != null
                    && seller.getBankDetails().getAccountHolderName() != null
                    && seller.getBankDetails().getIfscCode() != null
                    && seller.getBankDetails().getAccountNumber() != null
            ) {
                existingSeller.getBankDetails().setAccountHolderName(
                        seller.getBankDetails().getAccountHolderName()
                );
                existingSeller.getBankDetails().setAccountNumber(
                        seller.getBankDetails().getAccountNumber()
                );
                existingSeller.getBankDetails().setIfscCode(
                        seller.getBankDetails().getIfscCode()
                );
            }

            if (seller.getPickupAddress() != null
                    && seller.getPickupAddress().getAddress() != null
                    && seller.getPickupAddress().getPhone() != null
                    && seller.getPickupAddress().getCity() != null
                    && seller.getPickupAddress().getState() != null
            ) {
                existingSeller.getPickupAddress().setCity(
                        seller.getPickupAddress().getCity()

                );
                existingSeller.getPickupAddress().setPhone(
                        seller.getPickupAddress().getPhone()
                );
                existingSeller.getPickupAddress().setState(
                        seller.getPickupAddress().getState()
                );
                existingSeller.getPickupAddress().setAddress(
                        seller.getPickupAddress().getAddress()
                );
                existingSeller.getPickupAddress().setPinCode(
                        seller.getPickupAddress().getPinCode()
                );
            }
            if (seller.getGSTIN() != null) {
                existingSeller.setGSTIN(seller.getGSTIN());
            }
        }
        return sellerRepo.save(existingSeller);
    }

    @Override
    public void deleteSeller(Long id) throws Exception {
        Seller seller = getSellerById(id);
        sellerRepo.delete(seller);
    }

    @Override
    public Seller verifyEmail(String email, String otp) throws Exception {
        Seller seller = getSellerByEmail(email);
        seller.setEmailVerified(true);
        return sellerRepo.save(seller);
    }

    @Override
    public Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws Exception {
        Seller seller = getSellerById(sellerId);
        seller.setAccountStatus(status);
        return sellerRepo.save(seller);
    }
}
