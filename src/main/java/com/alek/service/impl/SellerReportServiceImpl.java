package com.alek.service.impl;

import com.alek.model.Seller;
import com.alek.model.SellerReport;
import com.alek.repository.SellerReportRepo;
import com.alek.service.SellerReportService;
import org.springframework.stereotype.Service;

@Service
public class SellerReportServiceImpl implements SellerReportService {

    private final SellerReportRepo sellerReportRepo;

    public SellerReportServiceImpl(SellerReportRepo sellerReportRepo) {
        this.sellerReportRepo = sellerReportRepo;
    }

    @Override
    public SellerReport getSellerReport(Seller seller) {

       SellerReport sr = sellerReportRepo.findBySellerId(seller.getId());

       if (sr == null){
           SellerReport newReport = new SellerReport();
           newReport.setSeller(seller);
              return sellerReportRepo.save(newReport);
       }
        return sr;
    }

    @Override
    public SellerReport updateSellerReport(SellerReport sellerReport) {
        return sellerReportRepo.save(sellerReport);
    }
}
