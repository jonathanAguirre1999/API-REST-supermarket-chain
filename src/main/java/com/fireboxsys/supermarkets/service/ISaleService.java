package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.SaleRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface ISaleService {

    SaleResponseDTO save(SaleRequestDTO newSaleDTO);
    SaleResponseDTO findById(Long id);
    Page<SaleResponseDTO> findByBranchId(Long branchId, Pageable pageable);
    Page<SaleResponseDTO> findAll(Pageable pageable);
    SaleResponseDTO update(Long id, SaleRequestDTO saleDTO);
    void delete(Long id);

}
