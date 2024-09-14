package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.*;
import com.HIT.StoreManagementApp.repository.SaleRepository;
import com.HIT.StoreManagementApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/sales")
public class SaleController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private BranchProductService BranchProductService;

    @Autowired
    private CustomerService customerService; // Inject customer service

    @Autowired
    private SaleRepository saleRepository;

    @PostMapping("/sell")
    public ResponseEntity<?> sellProduct(@RequestParam Long employeeId, @RequestParam Long productId,
                                         @RequestParam Long branchId, @RequestParam int quantity,
                                         @RequestParam Long customerId) {
        // Find Employee, Product, Branch, and Customer
        User employee = userService.findById(employeeId);
        Product product = productService.findById(productId);
        Branch branch = branchService.getBranchById(branchId);
        Customer customer = customerService.findById(customerId);

        if (employee == null || product == null || branch == null || customer == null) {
            return ResponseEntity.badRequest().body("Employee, Product, Branch, or Customer not found.");
        }

        // Update product stock after sale
        boolean success = BranchProductService.updateStock(branchId, productId, quantity);

        if (success) {
            // Calculate the sale price (twice the quantity sold)
            double price = product.getPrice() * 2 * quantity;

            // Create a new Sale record
            Sale sale = new Sale();
            sale.setProduct(product);
            sale.setEmployee(employee);
            sale.setBranch(branch);
            sale.setCustomer(customer); // Set the customer for the sale
            sale.setQuantity(quantity);
            sale.setPrice(price);
            sale.setSaleTime(LocalDateTime.now());

            // Save the Sale record
            saleRepository.save(sale);

            return ResponseEntity.ok("Product sold successfully.");
        } else {
            return ResponseEntity.badRequest().body("Sale failed: insufficient stock or product not found.");
        }
    }
}
