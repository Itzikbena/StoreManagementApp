package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.*;
import com.HIT.StoreManagementApp.repository.SaleRepository;
import com.HIT.StoreManagementApp.service.*;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.HIT.StoreManagementApp.model.Sale;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;





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

    @GetMapping("/report/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam String type,
                                                   @RequestParam(required = false) Long branchId,
                                                   @RequestParam(required = false) String product,
                                                   @RequestParam(required = false) String category) throws IOException {
        List<Sale> reportData = new ArrayList<>();

        switch (type) {
            case "branchSales":
                if (branchId != null) {
                    reportData = saleRepository.findByBranchId(branchId);
                }
                break;

            case "productSales":
                if (product != null) {
                    reportData = saleRepository.findByProduct_Name(product);
                }
                break;

            case "categorySales":
                if (category != null) {
                    reportData = saleRepository.findByProduct_Category(category);
                }
                break;

            default:
                return ResponseEntity.badRequest().body(null);
        }

        // Convert data to CSV with BOM
        StringBuilder csvBuilder = new StringBuilder("\uFEFF"); // Adding BOM for UTF-8 support
        csvBuilder.append("Product Name,Quantity,Price,Sale Date\n");
        for (Sale sale : reportData) {
            csvBuilder.append(sale.getProduct().getName()).append(",");
            csvBuilder.append(sale.getQuantity()).append(",");
            csvBuilder.append(sale.getPrice()).append(",");
            csvBuilder.append(sale.getSaleTime()).append("\n");
        }

        // Save CSV to file
        String fileName = "report.csv";
        Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), fileName);
        Files.write(filePath, csvBuilder.toString().getBytes(StandardCharsets.UTF_8));

        // Prepare the resource for download
        org.springframework.core.io.Resource resource = new FileSystemResource(filePath.toFile());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(filePath.toFile().length())
                .body(resource);
    }



    @GetMapping("/report")
    public ResponseEntity<?> generateReport(@RequestParam String type,
                                            @RequestParam(required = false) Long branchId,
                                            @RequestParam(required = false) String product,
                                            @RequestParam(required = false) String category) {
        List<Sale> reportData = new ArrayList<>();

        switch (type) {
            case "branchSales":
                if (branchId != null) {
                    reportData = saleRepository.findByBranchId(branchId);
                }
                break;

            case "productSales":
                if (product != null) {
                    reportData = saleRepository.findByProduct_Name(product);
                }
                break;

            case "categorySales":
                if (category != null) {
                    reportData = saleRepository.findByProduct_Category(category);
                }
                break;

            default:
                return ResponseEntity.badRequest().body("Invalid report type");
        }

        return ResponseEntity.ok(reportData);
    }



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

        // Check if any entity is not found
        if (employee == null || product == null || branch == null || customer == null) {
            response.put("success", false);
            response.put("message", "העובד, מוצר, סניף, או לקוח לא נמצאו.");

            // Create a new Log object for failed sale attempt
            Log log = new Log("ניסיון מכירה נכשל",
                    "ניסיון מכירה נכשל ע\"י עובד מספר: " + employeeId + ". סיבה: העובד, מוצר, סניף, או לקוח לא נמצאו.",
                    LocalDateTime.now(), branchId, 3);

            // Save the log using the method in Log class
            log.save();

            return ResponseEntity.badRequest().body(response);
        }

        // Update product stock after sale
        boolean success = BranchProductService.updateStock(branchId, productId, quantity);

        if (success) {
            // Calculate the sale price (product price multiplied by quantity)
            double price = product.getPrice() * quantity;

            // Create a new Sale record
            Sale sale = new Sale();
            sale.setProduct(product);
            sale.setEmployee(employee);
            sale.setBranch(branch);
            sale.setCustomer(customer); // Set the customer for the sale
            sale.setQuantity(quantity);
            sale.setPrice(price);
            sale.setSaleTime(LocalDateTime.now());

            // Process sale and increment customer purchases
            sale.processSale();

            // Save the updated customer (with incremented purchases)
            customerService.save(customer); // Save updated customer

            // Save the Sale record
            saleRepository.save(sale);

            // Create a new Log object for successful sale
            Log log = new Log("מכירת מוצר",
                    "מכירה בוצעה בהצלחה ע\"י עובד מספר: " + employeeId + " ללקוח מספר: " + customerId +
                            " - מוצר: " + product.getName() + ", כמות: " + quantity + ", מחיר כולל: " + price,
                    LocalDateTime.now(), branchId, 3);

            // Save the log using the method in Log class
            log.save();

            // Return success response with sale details
            response.put("success", true);
            response.put("message", "המוצר נמכר בהצלחה.");
            response.put("sale", sale); // Include the sale object in the response if needed
            return ResponseEntity.ok(response);
        } else {
            // Create a new Log object for failed sale due to stock issues
            Log log = new Log("מכירה נכשלה - בעיית מלאי",
                    "המכירה נכשלה עקב חוסר במלאי או המוצר לא נמצא. מזהה מוצר: " + productId +
                            ", מזהה סניף: " + branchId + ", כמות מבוקשת: " + quantity,
                    LocalDateTime.now(), branchId, 3);

            // Save the log using the method in Log class
            log.save();

            // Failed to update stock
            response.put("success", false);
            response.put("message", "המכירה נכשלה: חוסר במלאי או המוצר לא נמצא.");
            return ResponseEntity.badRequest().body(response);
        }
    }




}
