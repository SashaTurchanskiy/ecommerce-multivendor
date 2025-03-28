package com.alek.repository;

import com.alek.model.Seller;
import com.alek.model.SellerReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerReportRepo extends JpaRepository<SellerReport, Long > {

    SellerReport findBySellerId(Long id);
}
