package com.fireboxsys.supermarkets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fireboxsys.supermarkets.dto.ProductRequestDTO;
import com.fireboxsys.supermarkets.dto.ProductResponseDTO;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private ProductRequestDTO validRequestDTO;
    private ProductResponseDTO mockResponseDTO;
    private ObjectMapper objectMapper;
    private static final String DEFAULT_404_MESSAGE = "Product not found with id: #";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        validRequestDTO = new ProductRequestDTO();
        validRequestDTO.setName("Dr Pepper");
        validRequestDTO.setCategory("Beverages");
        validRequestDTO.setPrice(1.50);
        validRequestDTO.setStock(100);

        mockResponseDTO = new ProductResponseDTO();
        mockResponseDTO.setId(99L);
        mockResponseDTO.setName("Dr Pepper");
        mockResponseDTO.setCategory("Beverages");
        mockResponseDTO.setPrice(1.50);
        mockResponseDTO.setStock(100);

    }

    // ----------------------------------------------------- HAPPY PATHS -----------------------------------------------------//

    @Test
    void findAllProducts() throws Exception{
        ProductResponseDTO mockResponseDTO2 = new ProductResponseDTO();
        mockResponseDTO2.setId(50L);
        mockResponseDTO2.setName("Milk");
        mockResponseDTO2.setCategory("Beverages");
        mockResponseDTO2.setPrice(2.00);
        mockResponseDTO2.setStock(100);

        List<ProductResponseDTO> mockProducts = new ArrayList<>();
        mockProducts.add(mockResponseDTO);
        mockProducts.add(mockResponseDTO2);

        Page<ProductResponseDTO> mockPage = new PageImpl<>(mockProducts);

        when(productService.findAll(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(99L))
                .andExpect(jsonPath("$.content[0].name").value("Dr Pepper"))
                .andExpect(jsonPath("$.content[1].id").value(50L))
                .andExpect(jsonPath("$.content[1].name").value("Milk"));
    }

    @Test
    void findProductById() throws Exception{

        when(productService.findById(99L)).thenReturn(mockResponseDTO);

        mockMvc.perform(get("/api/products/{id}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.name").value("Dr Pepper"));
    }

    @Test
    void findTopProduct() throws Exception{

        when(productService.findTopProduct()).thenReturn(mockResponseDTO);

        mockMvc.perform(get("/api/products/top-product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.name").value("Dr Pepper"));

    }

    @Test
    void createNewProduct() throws Exception{

        when(productService.save(any(ProductRequestDTO.class))).thenReturn(mockResponseDTO);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.name").value("Dr Pepper"));
    }

    @Test
    void updateProduct() throws Exception{

        ProductRequestDTO updatedRequestDTO = new ProductRequestDTO();

        updatedRequestDTO.setName("Updated Product");
        updatedRequestDTO.setCategory("Updated Category");
        updatedRequestDTO.setPrice(50.00);
        updatedRequestDTO.setStock(19);

        ProductResponseDTO updatedResponseDTO = new ProductResponseDTO();
        updatedResponseDTO.setId(99L);
        updatedResponseDTO.setName(updatedRequestDTO.getName());
        updatedResponseDTO.setCategory(updatedRequestDTO.getCategory());
        updatedResponseDTO.setPrice(updatedRequestDTO.getPrice());
        updatedResponseDTO.setStock(updatedRequestDTO.getStock());

        when(productService.findById(99L)).thenReturn(mockResponseDTO);
        when(productService.update(anyLong(), any(ProductRequestDTO.class))).thenReturn(updatedResponseDTO);

        mockMvc.perform(put("/api/products/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.category").value("Updated Category"))
                .andExpect(jsonPath("$.price").value(50.00))
                .andExpect(jsonPath("$.stock").value(19));
    }

    @Test
    void deleteProduct() throws Exception{
        doNothing().when(productService).delete(anyLong());

        mockMvc.perform(delete("/api/products/{id}", 99L))
                .andExpect(status().isNoContent());
    }

    // ----------------------------------------------------- SAD PATHS -----------------------------------------------------//

    @Test
    void findProductById_whenProductNotFound_shouldReturn404() throws Exception{
        when(productService.findById(23L)).thenThrow(new NotFoundException(DEFAULT_404_MESSAGE + 23));

        mockMvc.perform(get("/api/products/{id}", 23L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllProducts_whenNoProductsFound_shouldReturn200() throws Exception{
        when(productService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void findTopProduct_whenNoProductsFound_shouldReturn404() throws Exception{
        when(productService.findTopProduct()).thenThrow(new NotFoundException("No sales recorded yet to find the most sold product."));

        mockMvc.perform(get("/api/products/top-product"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNewProduct_withInvalidData_shouldReturn400() throws Exception{
        ProductRequestDTO invalidRequestDTO = new ProductRequestDTO();
        invalidRequestDTO.setName("");
        invalidRequestDTO.setCategory("");
        invalidRequestDTO.setPrice(-10.00);
        invalidRequestDTO.setStock(-5);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_whenProductNotFound_shouldReturn404() throws Exception{
        ProductRequestDTO updatedRequestDTO = new ProductRequestDTO();
        updatedRequestDTO.setName("Updated Product");
        updatedRequestDTO.setCategory("Updated Category");
        updatedRequestDTO.setPrice(50.00);
        updatedRequestDTO.setStock(19);

        when(productService.update(eq(23L), any(ProductRequestDTO.class))).thenThrow(new NotFoundException(DEFAULT_404_MESSAGE + 23L));

        mockMvc.perform(put("/api/products/{id}", 23L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_withInvalidData_shouldReturn400() throws Exception{

        ProductRequestDTO invalidRequestDTO = new ProductRequestDTO();
        invalidRequestDTO.setName("");
        invalidRequestDTO.setCategory("");
        invalidRequestDTO.setPrice(0.00);
        invalidRequestDTO.setStock(0);

        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProduct_whenProductNotFound_shouldReturn404() throws Exception{
        doThrow(new NotFoundException(DEFAULT_404_MESSAGE)).when(productService).delete(23L);

        mockMvc.perform(delete("/api/products/{id}", 23L))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------- EDGE CASES -----------------------------------------------------//

    @Test
    void findProductById_whenIdIsLetter_shouldReturn400() throws Exception{
        mockMvc.perform(get("/api/products/{id}", "a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when JSON is malformed")
    void createNewProduct_withMalformedJson_shouldReturn400() throws Exception {

        String malformedJson = "{ \"name\": \"Dr Pepper\", \"price\": 1.50 ";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}