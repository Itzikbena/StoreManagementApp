package com.HIT.StoreManagementApp.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BranchProductId implements Serializable {

    private Long branchId;
    private Long productId;

    // Default constructor
    public BranchProductId() {}

    // Constructor with parameters
    public BranchProductId(Long branchId, Long productId) {
        this.branchId = branchId;
        this.productId = productId;
    }

    // Getters and setters
    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // Equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BranchProductId that = (BranchProductId) o;
        return Objects.equals(branchId, that.branchId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchId, productId);
    }
}
