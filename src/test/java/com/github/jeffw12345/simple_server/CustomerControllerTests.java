package com.github.jeffw12345.simple_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CustomerController.class)
public class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Customer customer = new Customer(
            "123",
            "John Doe",
            "123 Main St",
            "Apt 4B",
            "Manchester",
            "Greater Manchester",
            "UK",
            "M1 1AA"
    );

    @Test
    void whenObtainCustomerFromReferenceGivenIdInDatabase_thenBehavesAsExpected() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerReference("123");
        customer.setCustomerName("John Doe");

        when(customerService.getDetailsForCustomerFromReference("123")).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/search")
                        .param("customerReference", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerReference").value("123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void whenObtainCustomerFromReferenceGivenIdNotInDatabase_thenBehavesAsExpected()throws Exception {
        when(customerService.getDetailsForCustomerFromReference("123")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/search")
                        .param("customerReference", "123") // Use request parameter
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void whenProcessCustomerDetailsGivenCustomerDetailsNotInDatabase_thenBehavesAsExpected() throws Exception {
        when(customerService.addCustomerToDatabase(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(customer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerReference").value("123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void whenProcessCustomerDetailsGivenCustomerDetailsAlreadyInDatabase_thenBehavesAsExpected() throws Exception {
        when(customerService.addCustomerToDatabase(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        )).thenThrow(new RuntimeException("Customer with ID 123 already exists."));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isConflict());
    }

    @Test
    void givenBadRequest_thenReturns400StatusCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
