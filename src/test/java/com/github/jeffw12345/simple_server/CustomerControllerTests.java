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

import static org.mockito.ArgumentMatchers.any;
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
        when(customerService.getDetailsForCustomerFromReference("123")).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/search")
                        .param("customerReference", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerReference").value("123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void whenObtainCustomerFromReferenceGivenIdNotInDatabase_thenBehavesAsExpected() throws Exception {
        when(customerService.getDetailsForCustomerFromReference("123")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/search")
                        .param("customerReference", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void whenProcessCustomerDetailsGivenValidCustomerThatIsNotInDatabase_thenBehavesAsExpected() throws Exception {
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
                .andExpect(MockMvcResultMatchers.status().isCreated())
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

    // Tests for empty mandatory fields
    @Test
    void whenMandatoryFieldsAreEmptyInPostRequest_thenReturnsBadRequest() throws Exception {
        Customer customerWithBlankFields = new Customer();
        customerWithBlankFields.setCustomerReference("");
        customerWithBlankFields.setCustomerName("");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithBlankFields)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenMandatoryReferenceFieldIsEmptyInPostRequest_thenReturnsBadRequest() throws Exception {
        Customer customerWithBlankReference = new Customer();
        customerWithBlankReference.setCustomerReference("");
        customerWithBlankReference.setCustomerName("John Smith");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithBlankReference)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenMandatoryNameFieldIsEmptyInPostRequest_thenReturnsBadRequest() throws Exception {
        Customer customerWithBlankName = new Customer();
        customerWithBlankName.setCustomerReference("123");
        customerWithBlankName.setCustomerName("");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithBlankName)))
                .andExpect(status().isBadRequest());
    }

    // Tests for null mandatory fields
    @Test
    void whenMandatoryReferenceFieldIsNullInPostRequest_thenReturnsBadRequest() throws Exception {
        Customer customerWithNullReference = new Customer();
        customerWithNullReference.setCustomerReference(null);
        customerWithNullReference.setCustomerName("John Smith");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithNullReference)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenMandatoryNameFieldIsNullInPostRequest_thenReturnsBadRequest() throws Exception {
        Customer customerWithNullName = new Customer();
        customerWithNullName.setCustomerReference("123");
        customerWithNullName.setCustomerName(null);  // Null name

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithNullName)))
                .andExpect(status().isBadRequest());
    }

    // Tests for blank mandatory fields
    @Test
    void whenCustomerNameInGetRequestIsBlank_thenReturnsBadRequest() throws Exception {
        Customer customerWithBlankName = new Customer();
        customerWithBlankName.setCustomerReference("123");
        customerWithBlankName.setCustomerName("");  // Blank name

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithBlankName)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNonMandatoryFieldsAreEmptyInPostRequest_thenSucceeds() throws Exception {
        Customer customerWithBlankNonMandatoryFields = new Customer(
                "123",
                "customerName",
                "",
                "",
                "",
                "",
                "",
                ""
        );

        when(customerService.addCustomerToDatabase(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(customerWithBlankNonMandatoryFields);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithBlankNonMandatoryFields)))
                .andExpect(status().isCreated());
    }
    @Test
    void whenNonMandatoryFieldsAreNullInPostRequest_thenSucceeds() throws Exception {
        Customer customerWithNullNonMandatoryFields = new Customer(
                "123", // mandatory field
                "customerName", // mandatory field
                null, // addressLine1
                null, // addressLine2
                null, // town
                null, // county
                null, // country
                null  // postcode
        );

        when(customerService.addCustomerToDatabase(
                any(String.class),
                any(String.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(customerWithNullNonMandatoryFields);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithNullNonMandatoryFields)))
                .andExpect(status().isCreated());
    }
}
