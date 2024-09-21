package com.HIT.StoreManagementApp.service;

import com.HIT.StoreManagementApp.model.Customer;
import com.HIT.StoreManagementApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LogsService logsService;

    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    public Customer findById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.orElse(null);  // אם הלקוח לא נמצא, יוחזר null
    }

    public Customer createCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);

        logsService.addLog(
                "רישום לקוחות",
                "לקוח חדש נרשם: " + savedCustomer.getName(),
                null,
                1
        );


        return savedCustomer;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();  // מחזיר את כל הלקוחות ברשימה
    }
}
