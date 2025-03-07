package com.alek.service.impl;

import com.alek.domain.USER_ROLE;
import com.alek.model.Seller;
import com.alek.model.User;
import com.alek.repository.SellerRepo;
import com.alek.repository.UserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;
    private final SellerRepo sellerRepo;
    private static final String SELLER_PREFIX = "seller_";

    public CustomUserServiceImpl(UserRepo userRepo, SellerRepo sellerRepo) {
        this.userRepo = userRepo;
        this.sellerRepo = sellerRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.startsWith(SELLER_PREFIX)){
            String actualUsername = username.substring(SELLER_PREFIX.length());
            Seller seller = sellerRepo.findByEmail(actualUsername);

            if (seller != null){
                return buildUserDetails(seller.getEmail(), seller.getPassword(), seller.getRole());
            }

        }else {
            User user = userRepo.findByEmail(username);
            if (user != null){
                return buildUserDetails(user.getEmail(), user.getPassword(), user.getRole());
            }
        }
        throw new UsernameNotFoundException("User and seller not found with email -" + username);
    }

    private UserDetails buildUserDetails(String email, String password, USER_ROLE role) {
        if (role == null) role = USER_ROLE.ROLE_CUSTOMER;

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_"+role));

        return new org.springframework.security.core.userdetails.User(email, password, authorityList);


    }
}
