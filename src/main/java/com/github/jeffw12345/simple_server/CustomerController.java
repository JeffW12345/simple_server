package com.github.jeffw12345.simple_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> processCustomerDetails(@RequestBody Customer customer) {
        try {
            log.info("Received customer with reference: {}", customer.getCustomerReference());

            Customer addedCustomer = customerService.addCustomerToDatabase(
                    customer.getCustomerReference(),
                    customer.getCustomerName(),
                    customer.getAddressLine1(),
                    customer.getAddressLine2(),
                    customer.getTown(),
                    customer.getCounty(),
                    customer.getCountry(),
                    customer.getPostcode()
            );
            log.info("Customer with id " + customer.getCustomerReference() + " successfully added.");
            return ResponseEntity.ok(addedCustomer);
        } catch (RuntimeException e) {
            log.error("Customer with id " + customer.getCustomerReference() + " issue: " + e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Customer> obtainCustomerFromReference(
            @RequestParam(required = true) String customerReference) {
        Customer customer = customerService.getDetailsForCustomerFromReference(customerReference);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
