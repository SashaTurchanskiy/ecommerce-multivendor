package com.alek.service.impl;

import com.alek.model.HomeCategory;
import com.alek.repository.HomeCategoryRepo;
import com.alek.service.HomeCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeCategoryServiceImpl implements HomeCategoryService {

    private final HomeCategoryRepo homeCategoryRepo;

    public HomeCategoryServiceImpl(HomeCategoryRepo homeCategoryRepo) {
        this.homeCategoryRepo = homeCategoryRepo;
    }

    @Override
    public HomeCategory createHomeCategory(HomeCategory homeCategory) {
        return homeCategoryRepo.save(homeCategory);
    }

    @Override
    public List<HomeCategory> createCategories(List<HomeCategory> homeCategories) {
        if (homeCategoryRepo.findAll().isEmpty()){
            return homeCategoryRepo.saveAll(homeCategories);
        }
        return homeCategoryRepo.findAll();
    }

    @Override
    public HomeCategory updateHomeCategory(Long id, HomeCategory homeCategory) throws Exception {
        HomeCategory existingCategory = homeCategoryRepo.findById(id)
                .orElseThrow(()-> new Exception("Category not found"));
        if (homeCategory.getName()!=null){
            existingCategory.setImage(homeCategory.getImage());
        }
        if (homeCategory.getCategoryId()!=null){
            existingCategory.setCategoryId(homeCategory.getCategoryId());
        }
        return homeCategoryRepo.save(existingCategory);
    }

    @Override
    public List<HomeCategory> getAllHomeCategories() {
        return homeCategoryRepo.findAll();
    }
}
