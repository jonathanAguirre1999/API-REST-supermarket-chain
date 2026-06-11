package com.fireboxsys.supermarkets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetailsResponseDTO {

    private Long id;
    private Long saleId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double subtotal;

}
