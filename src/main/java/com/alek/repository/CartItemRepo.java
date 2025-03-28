package com.alek.repository;

import com.alek.model.Cart;
import com.alek.model.CartItem;
import com.alek.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    CartItem findByCartAndProductAndSize(Cart cart, Product product, String size);

}
