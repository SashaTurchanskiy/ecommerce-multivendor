package com.alek.service;

import com.alek.model.Product;
import com.alek.model.User;
import com.alek.model.Wishlist;

public interface WishlistService {

    Wishlist createWishlist(User user);
    Wishlist getWishlistByUserId(User user);
    Wishlist addProductToWishlist(User user, Product product);

}
