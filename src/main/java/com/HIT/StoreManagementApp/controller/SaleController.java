package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.*;
import com.HIT.StoreManagementApp.repository.SaleRepository;
import com.HIT.StoreManagementApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> sellProduct(@RequestParam Long employeeId, @RequestParam Long productId,
                                                           @RequestParam Long branchId, @RequestParam int quantity,
                                                           @RequestParam Long customerId) {
        Map<String, Object> response = new HashMap<>();

        // Find Employee, Product, Branch, and Customer
        User employee = userService.findById(employeeId);
        Product product = productService.findById(productId);
        Branch branch = branchService.getBranchById(branchId);
        Customer customer = customerService.findById(customerId);

        if (employee == null || product == null || branch == null || customer == null) {
            response.put("success", false);
            response.put("message", "Employee, Product, Branch, or Customer not found.");
            return ResponseEntity.badRequest().body(response);
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

            // Return success response with sale details
            response.put("success", true);
            response.put("message", "Product sold successfully.");
            response.put("sale", sale); // Include the sale object in the response if needed
            return ResponseEntity.ok(response);
        } else {
            // Failed to update stock
            response.put("success", false);
            response.put("message", "Sale failed: insufficient stock or product not found.");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
