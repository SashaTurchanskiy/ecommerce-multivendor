package com.alek.service;

import com.alek.model.Home;
import com.alek.model.HomeCategory;

import java.util.List;

public interface HomeService {
    public Home createHomePageData(List<HomeCategory> allCategories);
}
