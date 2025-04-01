package com.alek.service.impl;

import com.alek.exception.CartItemException;
import com.alek.model.CartItem;
import com.alek.model.User;
import com.alek.repository.CartItemRepo;
import com.alek.service.CartItemService;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepo cartItemRepo;

    public CartItemServiceImpl(CartItemRepo cartItemRepo) {
        this.cartItemRepo = cartItemRepo;
    }

    @Override
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws Exception {
        CartItem item = findCartItemById(id);

        User cartItemUser = item.getCart().getUser();

        if (cartItemUser.getId().equals(userId)){
            item.setQuantity(cartItem.getQuantity());
            item.setMrpPrice(item.getQuantity() * item.getProduct().getMrpPrice());
            item.setSellingPrice(item.getQuantity() * item.getProduct().getSellingPrice());

            return cartItemRepo.save(item);
        }

        throw new CartItemException("You cant update this cart item");
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) throws Exception {
        CartItem item = findCartItemById(cartItemId);

        User cartItemUser = item.getCart().getUser();

        if (cartItemUser.getId().equals(userId)){
            cartItemRepo.delete(item);
        }
        else throw new CartItemException("You cant delete this cart item");

    }

    @Override
    public CartItem findCartItemById(Long id) throws Exception {
        return cartItemRepo.findById(id).orElseThrow(()->
                new CartItemException("Cart item not found with id " + id));
    }
}
