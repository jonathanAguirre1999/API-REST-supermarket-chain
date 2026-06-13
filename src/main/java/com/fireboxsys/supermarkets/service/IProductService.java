package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.ProductRequestDTO;
import com.fireboxsys.supermarkets.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    ProductResponseDTO save(ProductRequestDTO newProductDTO);
    ProductResponseDTO findById(Long id);
    Page<ProductResponseDTO> findAll(Pageable pageable);
    ProductResponseDTO findTopProduct();
    ProductResponseDTO update(Long id, ProductRequestDTO productDTO);
    void delete(Long id);

}
