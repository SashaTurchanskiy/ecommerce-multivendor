package com.alek.service.impl;

import com.alek.exception.DealNotFoundException;
import com.alek.model.Deal;
import com.alek.model.HomeCategory;
import com.alek.repository.DealRepo;
import com.alek.repository.HomeCategoryRepo;
import com.alek.service.DealService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealServiceImpl implements DealService {

    private final DealRepo dealRepo;
    private final HomeCategoryRepo homeCategoryRepo;

    public DealServiceImpl(DealRepo dealRepo, HomeCategoryRepo homeCategoryRepo) {
        this.dealRepo = dealRepo;
        this.homeCategoryRepo = homeCategoryRepo;
    }

    @Override
    public List<Deal> getDeals() {
        return dealRepo.findAll();
    }

    @Override
    public Deal createDeal(Deal deal) {

        HomeCategory category = homeCategoryRepo.findById(deal.getCategory().getId()).orElse(null);
        Deal newDeal = dealRepo.save(deal);
        newDeal.setCategory(category);
        newDeal.setDiscount(deal.getDiscount());
        return dealRepo.save(newDeal);
    }

    @Override
    public Deal updateDeal(Deal deal, Long id) throws Exception {
        Deal existingDeal = dealRepo.findById(id).orElse(null);
        HomeCategory category = homeCategoryRepo.findById(deal.getCategory().getId()).orElse(null);
        if (existingDeal != null) {
            if (deal.getCategory() != null) {
                existingDeal.setDiscount(deal.getDiscount());
            }
            if (category != null) {
                existingDeal.setCategory(category);
            }
            return dealRepo.save(existingDeal);
        }
        throw new DealNotFoundException("Deal not found");
    }

    @Override
    public void deleteDeal(Long id) throws Exception {
        Deal deal = dealRepo.findById(id).orElseThrow(() ->
                new DealNotFoundException("Deal not found"));
        dealRepo.delete(deal);

    }

}
