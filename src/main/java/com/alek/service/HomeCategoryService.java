package com.alek.service;

import com.alek.model.HomeCategory;

import java.util.List;

public interface HomeCategoryService {
    HomeCategory createHomeCategory(HomeCategory homeCategory);
    List<HomeCategory> createCategories(List<HomeCategory> homeCategories);
    HomeCategory updateHomeCategory(Long id, HomeCategory homeCategory) throws Exception;
    List<HomeCategory> getAllHomeCategories();
}
