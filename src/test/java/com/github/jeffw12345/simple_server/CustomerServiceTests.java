package com.github.jeffw12345.simple_server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTests {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void givenDataIntegrityViolationExceptionInAddCustomerToDatabase_thenExpectedMessageThrown() {
        String customerReference = "REF123";
        String customerName = "John Doe";
        String addressLine1 = "123 Elm St";
        String addressLine2 = "Apt 4B";
        String town = "Manchester";
        String county = "Greater Manchester";
        String country = "UK";
        String postcode = "M1 1AA";

        doThrow(new DataIntegrityViolationException("Duplicate key"))
                .when(customerRepository)
                .save(any(Customer.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            customerService.addCustomerToDatabase(
                    customerReference, customerName, addressLine1, addressLine2, town, county, country, postcode);
        });

        assertEquals("Customer with ID REF123 already exists.", thrown.getMessage());
    }

}
