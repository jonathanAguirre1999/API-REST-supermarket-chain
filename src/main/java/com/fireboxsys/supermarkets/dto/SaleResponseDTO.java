package com.fireboxsys.supermarkets.dto;

import com.fireboxsys.supermarkets.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {

    private Long id;
    private LocalDateTime date;
    private Status status;
    private Double total;
    private Long branchId;
    private String branchName;
    private List<SaleDetailsResponseDTO> saleDetails;
}
