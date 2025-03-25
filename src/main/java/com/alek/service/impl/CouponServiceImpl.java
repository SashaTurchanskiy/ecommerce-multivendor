package com.alek.service.impl;

import com.alek.model.Cart;
import com.alek.model.Coupon;
import com.alek.model.User;
import com.alek.repository.CartRepo;
import com.alek.repository.CouponRepo;
import com.alek.repository.UserRepo;
import com.alek.service.CouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepo couponRepo;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;

    public CouponServiceImpl(CouponRepo couponRepo, CartRepo cartRepo, UserRepo userRepo) {
        this.couponRepo = couponRepo;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Cart applyCoupon(String code, double orderValue, User user) throws Exception {

        Coupon coupon = couponRepo.findByCode(code);
        Cart cart = cartRepo.findByUserId(user.getId());

        if (coupon == null){
            throw new Exception("Coupon not valid");
        }
        if (user.getUsedCoupons().contains(coupon)){
            throw new Exception("Coupon already used");
        }
        if (orderValue < coupon.getMinimumOrderValue()){
            throw new Exception("Order value too low " + coupon.getMinimumOrderValue());
        }
        if (coupon.isActive() && LocalDate.now().isAfter(coupon.getValidityStartDate()) && LocalDate.now().isBefore(coupon.getValidityEndDate()))
        {
            user.getUsedCoupons().add(coupon);
            userRepo.save(user);

        double discountPrice = (cart.getTotalSellingPrice() * coupon.getDiscountPercentage())/100;

        cart.setTotalSellingPrice(cart.getTotalSellingPrice() - discountPrice);
        cart.setCouponCode(code);
        cartRepo.save(cart);

        return cart;
        }
        throw new Exception("Coupon not valid");
    }

    @Override
    public Cart removeCoupon(String code, User user) throws Exception {
        Coupon coupon = couponRepo.findByCode(code);

        if (coupon == null){
            throw new Exception("Coupon not found");
        }
        Cart cart = cartRepo.findByUserId(user.getId());
        double discountPrice = (cart.getTotalSellingPrice() * coupon.getDiscountPercentage())/100;

        cart.setTotalSellingPrice(cart.getTotalSellingPrice() + discountPrice);
        cart.setCouponCode(null);

        return cartRepo.save(cart);
    }

    @Override
    public Coupon findCouponById(Long id) throws Exception {
        return couponRepo.findById(id).orElseThrow(()->
                new Exception("Coupon not found"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Coupon createCoupon(Coupon coupon) {
        return couponRepo.save(coupon);
    }

    @Override
    public List<Coupon> findAllCoupons() {
        return couponRepo.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCoupon(Long id) throws Exception {
        findCouponById(id);
        couponRepo.deleteById(id);
    }
}
