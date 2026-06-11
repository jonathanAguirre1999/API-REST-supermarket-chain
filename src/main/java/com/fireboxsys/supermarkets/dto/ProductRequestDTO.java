package com.fireboxsys.supermarkets.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequestDTO {

    @NotBlank( message = "Name cannot be empty")
    private String name;

    @NotBlank( message = "Category cannot be empty")
    private String category;

    @NotNull( message = "Price cannot be empty")
    @Positive( message = "Price cannot be negative or 0")
    private Double price;

    @NotNull( message = "Quantity cannot be empty or less than 1")
    @Min( value = 1, message = "Quantity cannot be less than 1")
    private Integer quantity;

}
