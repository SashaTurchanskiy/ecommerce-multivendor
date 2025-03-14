package com.alek.repository;

import com.alek.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    Category findByCategoryId(String categoryId);

}
