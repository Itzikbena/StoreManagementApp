package com.HIT.StoreManagementApp.repository;

import com.HIT.StoreManagementApp.model.BranchProduct;
import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchProductRepository extends JpaRepository<BranchProduct, Long> {
    Optional<BranchProduct> findByBranchAndProduct(Branch branch, Product product);
}
