package com.github.jeffw12345.simple_server;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> processCustomerDetails(@Valid @RequestBody Customer customer) {
        log.info("Received customer with reference: {}", customer.getCustomerReference());

        try {


            Optional<Customer> addedCustomer = Optional.ofNullable(customerService.addCustomerToDatabase(
                    customer.getCustomerReference(),
                    customer.getCustomerName(),
                    customer.getAddressLine1(),
                    customer.getAddressLine2(),
                    customer.getTown(),
                    customer.getCounty(),
                    customer.getCountry(),
                    customer.getPostcode()
            ));

            if (addedCustomer.isPresent()) {
                log.info("Customer with reference {} successfully added.", customer.getCustomerReference());
                return ResponseEntity.status(HttpStatus.CREATED).body(addedCustomer.get());
            } else {
                log.error("Customer could not be added with reference: {}", customer.getCustomerReference());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        } catch (RuntimeException e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Customer> obtainCustomerFromReference(
            @Valid @RequestParam String customerReference) {
        Optional<Customer> customer = Optional.ofNullable(customerService.getDetailsForCustomerFromReference(customerReference));

        return customer
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Customer not found with reference: {}", customerReference);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(null);
                });
    }
}
