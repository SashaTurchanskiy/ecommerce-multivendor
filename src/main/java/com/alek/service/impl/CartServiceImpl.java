package com.alek.service.impl;

import com.alek.model.Cart;
import com.alek.model.CartItem;
import com.alek.model.Product;
import com.alek.model.User;
import com.alek.repository.CartItemRepo;
import com.alek.repository.CartRepo;
import com.alek.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepo cartItemRepo;
    private final CartRepo cartRepo;

    public CartServiceImpl(CartItemRepo cartItemRepo, CartRepo cartRepo) {
        this.cartItemRepo = cartItemRepo;
        this.cartRepo = cartRepo;
    }

    @Override
    public CartItem addItemToCart(User user, Product product, String size, int quantity) {
        Cart cart = findUserCart(user);

        CartItem isPresent = cartItemRepo.findByCartAndProductAndSize(cart, product, size);

        if (isPresent == null){
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUserId(user.getId());
            cartItem.setSize(size);

            int totalPrice = quantity * product.getSellingPrice();
            cartItem.setSellingPrice(totalPrice);
            cartItem.setMrpPrice(quantity * product.getMrpPrice());

            cart.getCartItems().add(cartItem);
            cartItem.setCart(cart);

            return cartItemRepo.save(cartItem);

        }
        return isPresent;
    }

    @Override
    public Cart findUserCart(User user) {

        Cart cart = cartRepo.findByUserId(user.getId());

        int totalPrice = 0;
        int totalDiscountPrice = 0;
        int totalItems = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            totalPrice += cartItem.getMrpPrice();
            totalDiscountPrice += cartItem.getSellingPrice();
            totalItems += cartItem.getQuantity();
        }

        cart.setTotalMrpPrice(totalPrice);
        cart.setTotalItem(totalItems);
        cart.setTotalSellingPrice(totalDiscountPrice);
        cart.setDiscount(calculateDiscountPercentage(totalPrice, totalDiscountPrice));
        cart.setTotalItem(totalItems);
        return cart;
    }

    private int calculateDiscountPercentage(int mrpPrice, int sellingPrice) {
        if (mrpPrice <= 0){
            return 0;
        }
        double discount = mrpPrice - sellingPrice;
        double discountPercentage = (discount / mrpPrice) * 100;
        return (int) discountPercentage;

    }
}
