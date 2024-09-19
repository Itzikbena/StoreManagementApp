package com.HIT.StoreManagementApp.controller;

import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/branches")  // Mapping the /admin/branches URL to this controller
public class BranchController {

    @Autowired
    private BranchService branchService;  // Injecting BranchService to handle business logic

    // Method to handle POST requests to create a branch
    @PostMapping
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        // You can log the received data to confirm it's correct
        System.out.println("Received branch name: " + branch.getName());
        System.out.println("Received branch location: " + branch.getLocation());

        // Call the service layer to save the branch to the database
        Branch createdBranch = branchService.createBranch(branch);
        return ResponseEntity.ok(createdBranch);
    }

    // Method to get all branches from the database
    @GetMapping("/all")
    public ResponseEntity<List<Branch>> getAllBranches() {
        List<Branch> branches = branchService.getAllBranches();
        return ResponseEntity.ok(branches);
    }
}
