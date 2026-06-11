package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.ProductRequestDTO;
import com.fireboxsys.supermarkets.dto.ProductResponseDTO;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.model.Product;
import com.fireboxsys.supermarkets.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fireboxsys.supermarkets.mappers.Mapper.toDTO;

@Service
public class ProductService implements IProductService{

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO save(ProductRequestDTO newProductDTO) {
        if (newProductDTO == null) throw new IllegalArgumentException("Product cannot be null");
        Product p = new Product();
        p.setName(newProductDTO.getName());
        p.setCategory(newProductDTO.getCategory());
        p.setPrice(newProductDTO.getPrice());
        p.setQuantity(newProductDTO.getQuantity());
        return toDTO(productRepository.save(p));
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        return toDTO(productRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Product not found: id #" + id)));
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream().map(p -> toDTO(p)).toList();
    }

    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO productDTO) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found: id #" + id));
        p.setName(productDTO.getName());
        p.setCategory(productDTO.getCategory());
        p.setPrice(productDTO.getPrice());
        p.setQuantity(productDTO.getQuantity());
        return toDTO(productRepository.save(p));
    }

    @Override
    public void delete(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Product not found: id #" + id));
        productRepository.delete(p);
    }
}
