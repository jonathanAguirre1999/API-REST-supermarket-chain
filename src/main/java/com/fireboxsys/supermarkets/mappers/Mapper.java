package com.fireboxsys.supermarkets.mappers;

import com.fireboxsys.supermarkets.dto.BranchResponseDTO;
import com.fireboxsys.supermarkets.dto.ProductResponseDTO;
import com.fireboxsys.supermarkets.dto.SaleDetailsResponseDTO;
import com.fireboxsys.supermarkets.dto.SaleResponseDTO;
import com.fireboxsys.supermarkets.model.Branch;
import com.fireboxsys.supermarkets.model.Product;
import com.fireboxsys.supermarkets.model.Sale;
import com.fireboxsys.supermarkets.model.SaleDetails;

import java.util.List;

public class Mapper {

    public static ProductResponseDTO toDTO(Product p) {
        if (p == null) return null;
        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .category(p.getCategory())
                .price(p.getPrice())
                .stock(p.getStock())
                .build();
    }

    public static SaleResponseDTO toDTO(Sale s) {
        if (s == null) return null;

        return SaleResponseDTO.builder()
                .id(s.getId())
                .date(s.getDate())
                .status(s.getStatus())
                .total(s.getTotal())
                .branchId(s.getBranch().getId())
                .branchName(s.getBranch().getName())
                .saleDetails(s.getSaleDetails().stream()
                        .map(Mapper::toDTO)
                        .toList())
                .build();
    }

    public static BranchResponseDTO toDTO(Branch b) {
        if (b == null) return null;
        return BranchResponseDTO.builder()
                .id(b.getId())
                .name(b.getName())
                .address(b.getAddress())
                .build();
    }

    public static SaleDetailsResponseDTO toDTO(SaleDetails d) {
        if (d == null) return null;
        return SaleDetailsResponseDTO.builder()
                .id(d.getId())
                .saleId(d.getSale().getId())
                .productId(d.getProduct().getId())
                .productName(d.getProduct().getName())
                .quantity(d.getQuantity())
                .price(d.getPrice())
                .subtotal(d.getSubtotal())
                .build();
    }

}
