package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.SaleRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleResponseDTO;

import java.util.List;

public interface ISaleService {

    SaleResponseDTO save(SaleRequestDTO newSaleDTO);
    SaleResponseDTO findById(Long id);
    List<SaleResponseDTO> findAll();
    SaleResponseDTO update(Long id, SaleRequestDTO saleDTO);
    void delete(Long id);

}
