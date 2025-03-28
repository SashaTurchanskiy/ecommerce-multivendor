package com.alek.controller;

import com.alek.model.Deal;
import com.alek.response.ApiResponse;
import com.alek.service.DealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/deals")
public class DealController {

    private final DealService dealService;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }
    @PostMapping
    public ResponseEntity<Deal> createDeal(
            @RequestBody Deal deal){

        Deal createdDeal = dealService.createDeal(deal);
        return new ResponseEntity<>(createdDeal, HttpStatus.ACCEPTED);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Deal> updateDeal(
        @PathVariable Long id,
        @RequestBody Deal deal) throws Exception {

        Deal updatedDeal = dealService.updateDeal(deal, id);
        return ResponseEntity.ok(updatedDeal);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDeal(@PathVariable Long id) throws Exception {
        dealService.deleteDeal(id);

        ApiResponse res = new ApiResponse();
        res.setMessage("Deal deleted successfully");
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);

    }
}
