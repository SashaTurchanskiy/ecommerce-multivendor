package com.alek.repository;

import com.alek.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepo extends JpaRepository<Wishlist, Long> {

    Wishlist findByUserId(Long userId);
}
