package com.HIT.StoreManagementApp.repository;

import com.HIT.StoreManagementApp.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Find sales by branch ID
    List<Sale> findByBranchId(Long branchId);

    // Find sales by product name
    List<Sale> findByProduct_Name(String productName);

    // Find sales by product category (use nested property)
    List<Sale> findByProduct_Category(String category);
}

