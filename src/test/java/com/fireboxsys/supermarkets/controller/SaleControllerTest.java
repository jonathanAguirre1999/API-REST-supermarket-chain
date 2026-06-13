package com.fireboxsys.supermarkets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fireboxsys.supermarkets.dto.SaleDetailsRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleDetailsResponseDTO;
import com.fireboxsys.supermarkets.dto.SaleRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleResponseDTO;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.model.Branch;
import com.fireboxsys.supermarkets.model.Product;
import com.fireboxsys.supermarkets.service.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaleController.class)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService saleService;

    private ObjectMapper objectMapper;
    private SaleRequestDTO validRequestDTO;
    private SaleDetailsRequestDTO validDetailsRequestDTO;
    private SaleResponseDTO mockResponseDTO;
    private SaleDetailsResponseDTO mockDetailsResponseDTO;
    private Branch mockBranch;
    private Product mockProduct;
    private static final String DEFAULT_404_MESSAGE = "Sale not found with id: #";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockBranch = new Branch();
        mockBranch.setId(1L);
        mockBranch.setName("Main Branch");
        mockBranch.setAddress("123 Main St");

        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Milk");
        mockProduct.setCategory("Beverages");
        mockProduct.setPrice(2.00);
        mockProduct.setStock(100);

        validRequestDTO = new SaleRequestDTO();
        validRequestDTO.setBranchId(mockBranch.getId());

        validDetailsRequestDTO = new SaleDetailsRequestDTO();
        validDetailsRequestDTO.setProductId(mockProduct.getId());
        validDetailsRequestDTO.setQuantity(5);

        validRequestDTO.setDetails(List.of(validDetailsRequestDTO));

        // RESPONSES
        mockResponseDTO = new SaleResponseDTO();
        mockResponseDTO.setId(1L);
        mockResponseDTO.setDate(LocalDateTime.now());
        mockResponseDTO.setBranchId(mockBranch.getId());
        mockResponseDTO.setBranchName(mockBranch.getName());

        mockDetailsResponseDTO = new SaleDetailsResponseDTO();
        mockDetailsResponseDTO.setId(1L);
        mockDetailsResponseDTO.setProductId(mockProduct.getId());
        mockDetailsResponseDTO.setProductName(mockProduct.getName());
        mockDetailsResponseDTO.setPrice(mockProduct.getPrice());
        mockDetailsResponseDTO.setQuantity(5);
        mockDetailsResponseDTO.setSubtotal(20.00);
        mockDetailsResponseDTO.setSaleId(mockResponseDTO.getId());

        mockResponseDTO.setSaleDetails(List.of(mockDetailsResponseDTO));
        mockResponseDTO.setTotal(10.00);


    }

    // ----------------------------------------------------- HAPPY PATHS -----------------------------------------------------//

    @Test
    void findAllSales() throws Exception{
        List<SaleResponseDTO> mockSales = List.of(mockResponseDTO);

        Page<SaleResponseDTO> mockPage = new PageImpl<>(mockSales);

        when(saleService.findAll(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/sales"))
                .andExpect(status().isOk());
    }

    @Test
    void findSaleById() throws Exception{
        when(saleService.findById(1L)).thenReturn(mockResponseDTO);

        mockMvc.perform(get("/api/sales/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findSalesByBranchId() throws Exception{
        List<SaleResponseDTO> mockSales = List.of(mockResponseDTO);

        Page<SaleResponseDTO> mockPage = new PageImpl<>(mockSales);

        when(saleService.findByBranchId(eq(1L), any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/sales/branch/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void registerSale() throws Exception{
        when(saleService.save(any(SaleRequestDTO.class))).thenReturn(mockResponseDTO);

        mockMvc.perform(post("/api/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.branchId").value(1L))
                .andExpect(jsonPath("$.branchName").value("Main Branch"))
                .andExpect(jsonPath("$.saleDetails.length()").value(1))
                .andExpect(jsonPath("$.saleDetails[0].id").value(1L))
                .andExpect(jsonPath("$.saleDetails[0].productName").value("Milk"))
                .andExpect(jsonPath("$.saleDetails[0].subtotal").value(20.00));
    }

    @Test
    void deleteSale() throws Exception{
        doNothing().when(saleService).delete(1L);

        mockMvc.perform(delete("/api/sales/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    // ----------------------------------------------------- SAD PATHS -----------------------------------------------------//

    @Test
    void findSaleById_whenSaleNotFound_shouldReturn404() throws Exception{
        when(saleService.findById(5L)).thenThrow(new NotFoundException(DEFAULT_404_MESSAGE + 5L));

        mockMvc.perform(get("/api/sales/{id}", 5L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findSalesByBranchId_whenBranchNotFound_shouldReturn404() throws Exception{
        when(saleService.findByBranchId(eq(5l), any(Pageable.class))).thenThrow(new NotFoundException("Branch not found with ID: #" + 5L));

        mockMvc.perform(get("/api/sales/branch/{id}", 5L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findSalesByBranchId_whenBranchHasNoSales_shouldReturn200() throws Exception{
        Branch mockBranch2 = new Branch();
        mockBranch2.setId(3L);
        mockBranch2.setName("Second Branch");
        mockBranch2.setAddress("456 Second St");

        when(saleService.findByBranchId(eq(3L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/sales/branch/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void registerSale_whenSaleDetailsInvalid_shouldReturn400() throws Exception{
        validRequestDTO.setDetails(List.of());

        mockMvc.perform(post("/api/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerSale_withInvalidData_shouldReturn400() throws Exception{
        SaleRequestDTO invalidRequestDTO = new SaleRequestDTO();
        invalidRequestDTO.setBranchId(null);
        invalidRequestDTO.setDetails(List.of(validDetailsRequestDTO));

        mockMvc.perform(post("/api/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSale_whenSaleNotFound_shouldReturn404() throws Exception{
        doThrow(new NotFoundException(DEFAULT_404_MESSAGE + 8L)).when(saleService).delete(8L);

        mockMvc.perform(delete("/api/sales/{id}", 8L))
                .andExpect(status().isNotFound());
    }

    //------------------------------------------------ EDGE CASES -----------------------------------------------------//

    @Test
    void findSaleById_whenIdIsLetter_shouldReturn400() throws Exception{
        mockMvc.perform(get("/api/sales/{id}", "a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerSale_withMalformedJson_shouldReturn400() throws Exception{
        String malformedSaleJson = "{ \"branchId\": 1, \"saleDetails\": [ { \"productId\": 99, \"quantity\": 2 } ";

        mockMvc.perform(post("/api/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedSaleJson))
                .andExpect(status().isBadRequest());
    }
}