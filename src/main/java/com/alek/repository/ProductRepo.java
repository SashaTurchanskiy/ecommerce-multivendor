package com.alek.repository;

import com.alek.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findBySellerId(Long Id);

    @Query("SELECT p FROM Product p WHERE (:query is null or lower(p.title) " +
            "LIKE lower(concat('%', :query, '%'))) " +
            "or (:query is null or lower(p.category.name) " +
            "LIKE lower(concat('%', :query, '%')))")
    List<Product> searchProduct(@Param("query") String query);
}
