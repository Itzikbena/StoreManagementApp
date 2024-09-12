
package com.HIT.StoreManagementApp.repository;

import com.HIT.StoreManagementApp.model.BranchProduct;
import com.HIT.StoreManagementApp.model.BranchProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchProductRepository extends JpaRepository<BranchProduct, BranchProductId> {
}
