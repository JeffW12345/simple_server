package com.github.jeffw12345.simple_server;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Customer addCustomerToDatabase(String customerReference,
                                          String customerName,
                                          String addressLine1,
                                          String addressLine2,
                                          String town,
                                          String county,
                                          String country,
                                          String postcode) {


        Customer customer = new Customer(
                customerReference,
                customerName,
                addressLine1,
                addressLine2,
                town,
                county,
                country,
                postcode
        );

        try {
            log.info("About to add customer with id: " + customer.getCustomerReference());
            return customerRepository.save(customer);
        } catch (DataIntegrityViolationException ignored) {
            throw new RuntimeException("Customer with ID " + customer.getCustomerReference() + " already exists.");
        }
    }

    public Customer getDetailsForCustomerFromReference(String customerReference) {
        return customerRepository.findById(customerReference).orElse(null);
    }
}

