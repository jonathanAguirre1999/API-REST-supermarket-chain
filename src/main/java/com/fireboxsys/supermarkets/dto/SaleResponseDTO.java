package com.fireboxsys.supermarkets.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private Status status;
    private Double total;
    private Long branchId;
    private String branchName;
    private List<SaleDetailsResponseDTO> saleDetails;
}
