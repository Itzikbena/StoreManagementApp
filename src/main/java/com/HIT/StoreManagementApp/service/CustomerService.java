package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.Customer;
import com.HIT.StoreManagementApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    public void save(Customer customer) {
        customerRepository.save(customer); // Assuming customerRepository is injected
    }

    @Autowired
    private CustomerRepository customerRepository;

    // Method to find a customer by ID
    public Customer findById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.orElse(null);  // Returns null if the customer is not found
    }

    // Method to create a new customer
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // Method to get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();  // Returns a list of all customers
    }
}
