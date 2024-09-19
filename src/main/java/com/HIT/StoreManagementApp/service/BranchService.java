package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.Branch;
import com.HIT.StoreManagementApp.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    // Method to get all branches
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }


    @Autowired
    private BranchRepository branchRepository;

    // Method to create a branch and save it to the database
    public Branch createBranch(Branch branch) {
        return branchRepository.save(branch);  // Save the branch to the database
    }

    // Method to get a branch by its ID
    public Branch getBranchById(Long branchId) {
        return branchRepository.findById(branchId).orElse(null);  // Fetch the branch by ID or return null if not found
    }
}
