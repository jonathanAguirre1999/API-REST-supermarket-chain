package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.ProductRequestDTO;
import com.fireboxsys.supermarkets.dto.ProductResponseDTO;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.model.*;
import com.fireboxsys.supermarkets.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product mockProduct;
    private ProductRequestDTO validRequestDTO;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setId(99L);
        mockProduct.setName("Dr Pepper");
        mockProduct.setCategory("Beverages");
        mockProduct.setPrice(1.50);
        mockProduct.setStock(100);

        validRequestDTO = new ProductRequestDTO();
        validRequestDTO.setName("Dr Pepper");
        validRequestDTO.setCategory("Beverages");
        validRequestDTO.setPrice(1.50);
        validRequestDTO.setStock(100);
    }

    //----------------------------------------------- HAPPY PATHS -----------------------------------------------------//

    @Test
    @DisplayName("Should save a product")
    void save() {

        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductResponseDTO responseDTO = productService.save(validRequestDTO);

        assertNotNull(responseDTO, "Response should not be null");

        assertEquals(99L, responseDTO.getId(), "ID should be set to 99");
        assertEquals("Dr Pepper", responseDTO.getName(), "Name should be set to Dr Pepper");
        assertEquals("Beverages", responseDTO.getCategory(), "Category should be set to Beverages");
        assertEquals(1.50, responseDTO.getPrice(), "Price should be set to 1.50");
        assertEquals(100, responseDTO.getStock(), "Stock should be set to 100");

    }

    @Test
    @DisplayName("Should successfully return a product when ID is correct")
    void findById() {
        when(productRepository.findById(99L)).thenReturn(Optional.of(mockProduct));

        ProductResponseDTO responseDTO = productService.findById(99L);

        assertNotNull(responseDTO, "Response should not be null");
        assertEquals(99L, responseDTO.getId(), "ID should be set to 99");
        assertEquals("Dr Pepper", responseDTO.getName(), "Name should be set to Dr Pepper");
        assertEquals("Beverages", responseDTO.getCategory(), "Category should be set to Beverages");
        assertEquals(1.50, responseDTO.getPrice(), "Price should be set to 1.50");
        assertEquals(100, responseDTO.getStock(), "Stock should be set to 100");
    }

    @Test
    @DisplayName("Should return all products")
    void findAll() {
        Product mockProduct2 = new Product();
        mockProduct2.setId(50L);
        mockProduct2.setName("Milk");
        mockProduct2.setCategory("Beverages");
        mockProduct2.setPrice(2.00);
        mockProduct2.setStock(100);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(mockProduct);
        mockProducts.add(mockProduct2);

        Page<Product> mockPage = new PageImpl<>(mockProducts);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<ProductResponseDTO> responseDTOList = productService.findAll(Pageable.unpaged());

        assertNotNull(responseDTOList, "Response should not be null");
        assertFalse(responseDTOList.isEmpty(), "Response should not be empty");
        assertEquals(2, responseDTOList.getTotalElements(), "Total elements should match");
        assertEquals(2, responseDTOList.getNumberOfElements(), "Number of elements should match");

        assertEquals(99L, responseDTOList.getContent().get(0).getId(), "ID should be set to 99");
        assertEquals("Dr Pepper", responseDTOList.getContent().get(0).getName(), "Name should be set to Dr Pepper");
        assertEquals("Beverages", responseDTOList.getContent().get(0).getCategory(), "Category should be set to Beverages");
        assertEquals(1.50, responseDTOList.getContent().get(0).getPrice(), "Price should be set to 1.50");
        assertEquals(100, responseDTOList.getContent().get(0).getStock(), "Stock should be set to 100");

        assertEquals(50L, responseDTOList.getContent().get(1).getId(), "ID should be set to 50");
        assertEquals("Milk", responseDTOList.getContent().get(1).getName(), "Name should be set to Milk");
        assertEquals("Beverages", responseDTOList.getContent().get(1).getCategory(), "Category should be set to Beverages");
        assertEquals(2.00, responseDTOList.getContent().get(1).getPrice(), "Price should be set to 2.00");
        assertEquals(100, responseDTOList.getContent().get(1).getStock(), "Stock should be set to 100");
    }

    @Test
    @DisplayName("Should return the top sold product")
    void findTopProduct() {
        when(productRepository.findTopProducts(any(Pageable.class))).thenReturn(List.of(mockProduct));

        ProductResponseDTO responseDTO = productService.findTopProduct();

        assertNotNull(responseDTO, "Response should not be null");
        assertEquals(99L, responseDTO.getId(), "ID should be set to 99");
        assertEquals("Dr Pepper", responseDTO.getName(), "Name should be set to Dr Pepper");

        verify(productRepository, times(1)).findTopProducts(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update product info when ID is correct and request is valid")
    void update() {

        when(productRepository.findById(99L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductRequestDTO mockUpdatedProduct = new ProductRequestDTO();
        mockUpdatedProduct.setName("Updated Product");
        mockUpdatedProduct.setCategory("Updated Category");
        mockUpdatedProduct.setPrice(3.00);
        mockUpdatedProduct.setStock(10);

        ProductResponseDTO responseDTO = productService.update(99L, mockUpdatedProduct);

        assertNotNull(responseDTO, "Response should not be null");

        assertEquals(99L, responseDTO.getId(), "ID should be the same as the original");
        assertEquals("Updated Product", responseDTO.getName(), "Name should be set to Updated Product");
        assertEquals("Updated Category", responseDTO.getCategory(), "Category should be set to Updated Category");
        assertEquals(3.00, responseDTO.getPrice(), "Price should be set to 3.00");
        assertEquals(10, responseDTO.getStock(), "Stock should be set to 10");
    }

    @Test
    @DisplayName("Should logically delete a product")
    void delete() {

        when(productRepository.findById(99L)).thenReturn(Optional.of(mockProduct));

        productService.delete(99L);
        verify(productRepository, times(1)).delete(mockProduct);
    }

    // ----------------------------------------------------- SAD PATHS -----------------------------------------------------//

    @Test
    @DisplayName("Should throw NotFoundException when product to find does not exist")
    void findById_whenProductNotFound_shouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.findById(99L));
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when trying to update a non-existent product")
    void update_whenProductNotFound_shouldThrowException() {
        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Ghost Product");
        updateDTO.setPrice(10.0);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.update(99L, updateDTO));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when trying to delete a non-existent product")
    void delete_whenProductNotFound_shouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.delete(99L));

        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when there are no top products (empty database or no sales)")
    void findTopProduct_whenNoProductsExist_shouldThrowException() {
        when(productRepository.findTopProducts(any(Pageable.class))).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> productService.findTopProduct());
    }
}