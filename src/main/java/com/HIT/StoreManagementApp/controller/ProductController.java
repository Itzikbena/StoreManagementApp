package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.Product;
import com.HIT.StoreManagementApp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @PostMapping("/add/{branchId}")
    public Product addProduct(@RequestBody Product product, @PathVariable Long branchId) {
        return productService.addProduct(product, branchId);
    }

    @GetMapping("/branch/{branchId}")
    public List<Product> getProductsByBranchId(@PathVariable Long branchId) {
        return productService.getProductsByBranchId(branchId);
    }


    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
