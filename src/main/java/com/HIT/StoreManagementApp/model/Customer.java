package com.HIT.StoreManagementApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long passport;
    private long phone;

    private String type = "New"; // Default type is "New"
    private int purchases = 0; // New field to track the number of purchases

    private String name;
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<Sale> sales; // A customer can have multiple sales

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPassport() {
        return passport;
    }

    public void setPassport(long passport) {
        this.passport = passport;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPurchases() {
        return purchases;
    }

    public void setPurchases(int purchases) {
        this.purchases = purchases;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    // Method to increment purchases and update customer type
    public void incrementPurchases() {
        this.purchases++;
        if (this.purchases == 2) {
            this.type = "circular";
        } else if (this.purchases >= 3) {
            this.type = "vip";
        }
    }
}
