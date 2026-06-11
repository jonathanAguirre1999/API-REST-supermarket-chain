package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.SaleRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleResponseDTO;
import com.fireboxsys.supermarkets.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService implements ISaleService{

    private final SaleRepository saleRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    public SaleResponseDTO save(SaleRequestDTO newSaleDTO) {
        return null;
    }

    @Override
    public SaleResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public List<SaleResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public SaleResponseDTO update(Long id, SaleRequestDTO saleDTO) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
