package com.alek.service.impl;

import com.alek.domain.HomeCategorySection;
import com.alek.model.Deal;
import com.alek.model.Home;
import com.alek.model.HomeCategory;
import com.alek.repository.DealRepo;
import com.alek.service.HomeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    private final DealRepo dealRepo;

    public HomeServiceImpl(DealRepo dealRepo) {
        this.dealRepo = dealRepo;
    }

    @Override
    public Home createHomePageData(List<HomeCategory> allCategories) {
        List<HomeCategory> gridCategories = allCategories.stream()
                .filter(category ->
                        category.getSection() == HomeCategorySection.GRID)
                .toList();

        List<HomeCategory> shopByCategories = allCategories.stream()
                .filter(category->
                        category.getSection() == HomeCategorySection.SHOP_BY_CATEGORIES)
                .toList();

        List<HomeCategory> electricCategories = allCategories.stream()
                .filter(category->
                        category.getSection() == HomeCategorySection.ELECTRIC_CATEGORIES)
                .toList();

        List<HomeCategory> dealCategories = allCategories.stream()
                .filter(category->
                        category.getSection() == HomeCategorySection.DEALS)
                .toList();

        List<Deal> createDeals = new ArrayList<>();

        if (dealRepo.findAll().isEmpty()){
            List<Deal> deals = allCategories.stream()
                    .filter(category-> category.getSection() == HomeCategorySection.DEALS)
                    .map(category -> new Deal(null, 10, category))
                    .toList();
            createDeals = dealRepo.saveAll(deals);
        }else createDeals = dealRepo.findAll();

        Home home = new Home();
        home.setGrid(gridCategories);
        home.setShopByCategories(shopByCategories);
        home.setElectricCategories(electricCategories);
        home.setDeals(createDeals);
        home.setDealCategories(dealCategories);

        return home;
    }
}
