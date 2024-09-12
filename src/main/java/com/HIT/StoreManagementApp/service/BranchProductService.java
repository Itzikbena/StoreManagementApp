package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.model.BranchProduct;
import com.HIT.StoreManagementApp.model.Product;
import com.HIT.StoreManagementApp.repository.BranchRepository;
import com.HIT.StoreManagementApp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.HIT.StoreManagementApp.repository.BranchProductRepository;

import java.util.Optional;

@Service
public class BranchProductService {

    @Autowired
    private BranchProductRepository branchProductRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ProductRepository productRepository;

    public boolean updateStock(Long branchId, Long productId, int quantity) {
        // Find Branch and Product
        Optional<Branch> branch = branchRepository.findById(branchId);
        Optional<Product> product = productRepository.findById(productId);

        if (branch.isPresent() && product.isPresent()) {
            // Find BranchProduct entry based on branch and product
            Optional<BranchProduct> branchProduct = branchProductRepository.findByBranchAndProduct(branch.get(), product.get());

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
        }

        // BranchProduct entry not found
        return false;
    }
}
