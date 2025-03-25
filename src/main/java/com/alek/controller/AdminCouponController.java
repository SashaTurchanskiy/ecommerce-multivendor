package com.alek.controller;

import com.alek.model.Cart;
import com.alek.model.Coupon;
import com.alek.model.User;
import com.alek.service.CartService;
import com.alek.service.CouponService;
import com.alek.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class AdminCouponController {

    private final CouponService couponService;
    private final UserService userService;
    private final CartService cartService;

    public AdminCouponController(CouponService couponService, UserService userService, CartService cartService) {
        this.couponService = couponService;
        this.userService = userService;
        this.cartService = cartService;
    }
    @PostMapping("/apply")
    public ResponseEntity<Cart> applyCoupon(
            @RequestParam String apply,
            @RequestParam String code,
            @RequestParam double orderValue,
            @RequestHeader ("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Cart cart;
        if (apply.equals("true")) {
            cart = couponService.applyCoupon(code, orderValue, user);
        } else {
            cart = couponService.removeCoupon(code, user);
        }
        return ResponseEntity.ok(cart);
    }

    //admin operation
    @PostMapping("/admin/create")
    public ResponseEntity<Coupon> createCoupon(
            @RequestBody Coupon coupon){
        return ResponseEntity.ok(coupon);
    }
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteCoupon(
            @PathVariable Long id) throws Exception {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Coupon deleted");
    }
    @GetMapping("/admin/all")
    public ResponseEntity<List<Coupon>> getAllCoupons(){
        List<Coupon> coupons = couponService.findAllCoupons();
        return ResponseEntity.ok(coupons);
    }
}
