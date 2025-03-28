package com.alek.service;

import com.alek.model.Cart;
import com.alek.model.CartItem;
import com.alek.model.Product;
import com.alek.model.User;

public interface CartService {

    public CartItem addItemToCart(
            User user,
            Product product,
            String size,
            int quantity
    );
    public Cart findUserCart(User user);
}
