package com.fireboxsys.supermarkets.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SaleRequestDTO {

    @NotNull( message = "Branch ID is required")
    private Long branchId;

    @NotEmpty( message = "Products cannot be empty")
    @Valid
    private List<SaleDetailsRequestDTO> details;

}
