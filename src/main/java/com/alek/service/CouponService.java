package com.alek.service;

import com.alek.model.Cart;
import com.alek.model.Coupon;
import com.alek.model.User;

import java.util.List;

public interface CouponService {

    Cart applyCoupon(String couponCode, double orderValue, User user) throws Exception;
    Cart removeCoupon(String code, User user) throws Exception;
    Coupon findCouponById(Long id) throws Exception;
    Coupon createCoupon(Coupon coupon);
    List<Coupon> findAllCoupons();
    void deleteCoupon(Long id) throws Exception;

}
