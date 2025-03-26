package com.alek.repository;

import com.alek.model.HomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeCategoryRepo extends JpaRepository<HomeCategory, Long> {

}
