package com.fireboxsys.supermarkets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String category;
    private Double price;
    private Integer stock;
}
