package com.github.jeffw12345.simple_server;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(name = "customer_reference")
    @JsonProperty("customerReference")
    @Setter
    private String customerReference;

    @Column(name = "customer_name")
    @JsonProperty("customerName")
    @Setter
    private String customerName;

    @Column(name = "address_line1")
    @JsonProperty("addressLine1")
    private String addressLine1;

    @Column(name = "address_line2")
    @JsonProperty("addressLine2")
    private String addressLine2;

    @Column(name = "town")
    @JsonProperty("town")
    private String town;

    @Column(name = "county")
    @JsonProperty("county")
    private String county;

    @Column(name = "country")
    @JsonProperty("country")
    private String country;

    @Column(name = "postcode")
    @JsonProperty("postcode")
    private String postcode;

}
