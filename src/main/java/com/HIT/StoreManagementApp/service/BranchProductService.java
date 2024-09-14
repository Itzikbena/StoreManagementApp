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

    /**
     * Updates the stock for a product in a branch.
     * @param branchId ID of the branch.
     * @param productId ID of the product.
     * @param quantity Quantity to subtract from the stock.
     * @return true if the stock was successfully updated, false if there was an issue (e.g., insufficient stock).
     */
    public boolean updateStock(Long branchId, Long productId, int quantity) {
        // Find the product in the branch
        Optional<Product> productOptional = productRepository.findByBranchIdAndId(branchId, productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            int currentStock = product.getQuantity(); // Assuming 'quantity' is the stock field in the product table

            if (currentStock >= quantity) {
                // Subtract the sold quantity
                product.setQuantity(currentStock - quantity);
                productRepository.save(product); // Save the updated stock in the product table
                System.out.println("Stock updated for product ID " + productId + " in branch ID " + branchId);
                return true;
            } else {
                // Insufficient stock
                System.out.println("Insufficient stock for product ID " + productId + " in branch ID " + branchId);
                return false;
            }
        } else {
            // Product not found for the given branch
            System.out.println("Product not found for branch ID " + branchId + " and product ID " + productId);
            return false;
        }
    }
}
