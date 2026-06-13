package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.ProductRequestDTO;
import com.fireboxsys.supermarkets.dto.ProductResponseDTO;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.mappers.Mapper;
import com.fireboxsys.supermarkets.model.Product;
import com.fireboxsys.supermarkets.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fireboxsys.supermarkets.mappers.Mapper.toDTO;

@Service
public class ProductService implements IProductService{

    private final ProductRepository productRepository;
    private static final String DEFAULT_PRODUCT_NOT_FOUND_MESSAGE = "Product not found: id#";

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO newProductDTO) {
        if (newProductDTO == null) throw new IllegalArgumentException("Product cannot be null");
        Product p = new Product();
        p.setName(newProductDTO.getName());
        p.setCategory(newProductDTO.getCategory());
        p.setPrice(newProductDTO.getPrice());
        p.setStock(newProductDTO.getStock());
        return toDTO(productRepository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        return toDTO(productRepository.findById(id).orElseThrow(() ->
                new NotFoundException(DEFAULT_PRODUCT_NOT_FOUND_MESSAGE + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(Mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findTopProduct() {
        List<Product> products = productRepository.findTopProducts(PageRequest.of(0, 1));

        if (products.isEmpty()) throw new NotFoundException("No sales recorded yet to find the most sold product.");

        return toDTO(products.get(0));
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO productDTO) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException(DEFAULT_PRODUCT_NOT_FOUND_MESSAGE + id));
        p.setName(productDTO.getName());
        p.setCategory(productDTO.getCategory());
        p.setPrice(productDTO.getPrice());
        p.setStock(productDTO.getStock());
        return toDTO(productRepository.save(p));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() ->
                new NotFoundException(DEFAULT_PRODUCT_NOT_FOUND_MESSAGE+ id));
        productRepository.delete(p);
    }
}
