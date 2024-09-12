package com.HIT.StoreManagementApp.repository;

import com.HIT.StoreManagementApp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByName(String name);
}