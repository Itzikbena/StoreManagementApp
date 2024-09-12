
package com.HIT.StoreManagementApp.model;

import jakarta.persistence.*;

@Entity
public class BranchProduct {

    @EmbeddedId
    private BranchProductId id = new BranchProductId();

    @ManyToOne
    @MapsId("branchId")
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    private int branchStock;

    // Getters and Setters
    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getBranchStock() {
        return branchStock;
    }

    public void setBranchStock(int branchStock) {
        this.branchStock = branchStock;
    }
}
