package com.HIT.StoreManagementApp.repository;

import com.HIT.StoreManagementApp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByName(String name);
    Optional<Product> findByBranchIdAndId(Long branchId, Long id);

}