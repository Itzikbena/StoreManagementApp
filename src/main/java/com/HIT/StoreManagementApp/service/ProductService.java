package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.model.Product;
import com.HIT.StoreManagementApp.repository.BranchRepository;
import com.HIT.StoreManagementApp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BranchRepository branchRepository;

    // Method to find a Product by its ID
    public Product findById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null); // Return the product if found, otherwise return null
    }
    public Product addProduct(Product product, Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        product.setBranch(branch);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}

