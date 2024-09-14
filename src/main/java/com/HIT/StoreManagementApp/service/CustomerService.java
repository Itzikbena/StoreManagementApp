package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.Customer;
import com.HIT.StoreManagementApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer findById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.orElse(null);  // Returns null if the customer is not found
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
