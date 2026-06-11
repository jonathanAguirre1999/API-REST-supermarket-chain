package com.fireboxsys.supermarkets.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchRequestDTO {

    @NotBlank( message = "Name cannot be empty")
    private String name;

    @NotBlank( message = "Address cannot be empty")
    private String address;
}
