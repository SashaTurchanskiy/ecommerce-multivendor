package com.alek.service;

import com.alek.model.Seller;
import com.alek.model.SellerReport;

public interface SellerReportService {

    SellerReport getSellerReport(Seller seller);
    SellerReport updateSellerReport(SellerReport sellerReport);
}
