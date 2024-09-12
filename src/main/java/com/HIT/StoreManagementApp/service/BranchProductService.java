package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.BranchProduct;
import com.HIT.StoreManagementApp.model.BranchProductId; // Correct import for BranchProductId
import com.HIT.StoreManagementApp.repository.BranchProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BranchProductService {

    @Autowired
    private BranchProductRepository branchProductRepository;

    public boolean updateStock(Long branchId, Long productId, int quantity) {
        // Correct usage of BranchProductId
        Optional<BranchProduct> branchProduct = branchProductRepository.findById(new BranchProductId(branchId, productId));

        if (branchProduct.isPresent()) {
            BranchProduct bp = branchProduct.get();
            int currentStock = bp.getBranchStock();

            if (currentStock >= quantity) {
                bp.setBranchStock(currentStock - quantity);
                branchProductRepository.save(bp);
                return true;
            } else {
                // Insufficient stock
                return false;
            }
        }

        // BranchProduct entry not found
        return false;
    }
}
