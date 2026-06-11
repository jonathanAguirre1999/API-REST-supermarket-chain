package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.ProductRequestDTO;
import com.fireboxsys.supermarkets.dto.ProductResponseDTO;

import java.util.List;

public interface IProductService {

    ProductResponseDTO save(ProductRequestDTO newProductDTO);
    ProductResponseDTO findById(Long id);
    List<ProductResponseDTO> findAll();
    ProductResponseDTO update(Long id, ProductRequestDTO productDTO);
    void delete(Long id);

}
