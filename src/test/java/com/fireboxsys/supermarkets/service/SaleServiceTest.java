package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.SaleDetailsRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleResponseDTO;
import com.fireboxsys.supermarkets.model.*;
import com.fireboxsys.supermarkets.repository.BranchRepository;
import com.fireboxsys.supermarkets.repository.ProductRepository;
import com.fireboxsys.supermarkets.repository.SaleRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SaleService saleService;

    //MOCK OBJECTS
    private Product mockProduct;
    private Branch mockBranch;
    private Sale mockSale;
    private SaleDetails mockSaleDetails;
    private SaleRequestDTO validRequestDTO;
    private SaleDetailsRequestDTO validDetailsRequestDTO;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setId(99L);
        mockProduct.setName("Dr Pepper");
        mockProduct.setCategory("Beverages");
        mockProduct.setPrice(1.50);
        mockProduct.setStock(100);

        mockBranch = new Branch();
        mockBranch.setId(1L);
        mockBranch.setName("North Branch");
        mockBranch.setAddress("123 Fake St");

        validDetailsRequestDTO = new SaleDetailsRequestDTO();
        validDetailsRequestDTO.setProductId(mockProduct.getId());
        validDetailsRequestDTO.setQuantity(5);

        validRequestDTO = new SaleRequestDTO();
        validRequestDTO.setBranchId(mockBranch.getId());
        validRequestDTO.setDetails(List.of(validDetailsRequestDTO));

        mockSaleDetails = new SaleDetails();
        mockSaleDetails.setId(500L);
        mockSaleDetails.setProduct(mockProduct);
        mockSaleDetails.setQuantity(5);
        mockSaleDetails.setSubtotal(7.50);

        mockSale = new Sale();
        mockSale.setId(10L);
        mockSale.setDate(LocalDateTime.now());
        mockSale.setBranch(mockBranch);
        mockSale.setTotal(7.50);
        mockSale.setSaleDetails(List.of(mockSaleDetails));

        mockSaleDetails.setSale(mockSale);
    }

    // -------------------------- HAPPY PATH ---------------------------------------------------//

    @Test
    @DisplayName("Should successfully save a sale and deduct stock when inventory is sufficient")
    void save() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));
        when(productRepository.findById(99L)).thenReturn(Optional.of(mockProduct));

        Sale saved = new Sale();
        saved.setId(100L);
        saved.setDate(LocalDateTime.now());
        saved.setBranch(mockBranch);
        saved.setSaleDetails(List.of(mockSaleDetails));
        saved.setTotal(7.50);

        when(saleRepository.save(any(Sale.class))).thenReturn(saved);

        SaleResponseDTO responseDTO = saleService.save(validRequestDTO);

        assertNotNull(responseDTO, "Response should not be null");
        assertEquals(100L, responseDTO.getId(), "Sale ID should match");
        assertEquals(7.50, responseDTO.getTotal(), "Total should match and be perfectly calculated");

        assertEquals(95, mockProduct.getStock(), "Stock should be decremented by 5");
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should successfully return a SaleResponseDTO when ID is correct")
    void findById() {
        when(saleRepository.findById(10L)).thenReturn(Optional.of(mockSale));

        SaleResponseDTO responseDTO = saleService.findById(10L);

        assertNotNull(responseDTO, "Response should not be null");
        assertEquals(10L, responseDTO.getId(), "Sale ID should match");
        assertEquals(7.50, responseDTO.getTotal(), "Total should match and be perfectly calculated");

        assertFalse(responseDTO.getSaleDetails().isEmpty(), "Sale details should not be empty");
        assertEquals(1, responseDTO.getSaleDetails().size(), "Sale details should have 1 item");
        assertEquals(500L, responseDTO.getSaleDetails().get(0).getId(), "Sale detail ID should match");
        assertEquals(99L, responseDTO.getSaleDetails().get(0).getProductId(), "Product ID should match");
        assertEquals(5, responseDTO.getSaleDetails().get(0).getQuantity(), "Quantity should match");
        assertEquals("Dr Pepper", responseDTO.getSaleDetails().get(0).getProductName(), "Product name should be mapped");
        assertEquals(7.50, responseDTO.getSaleDetails().get(0).getSubtotal(), "Subtotal should match");
    }

    @Test
    @DisplayName("Should successfully return all sales")
    void findAll() {
        Product mockProduct2 = new Product();
        mockProduct2.setId(50L);
        mockProduct2.setName("Milk");
        mockProduct2.setCategory("Beverages");
        mockProduct2.setPrice(2.00);
        mockProduct2.setStock(100);

        SaleDetails mockSaleDetails2 = new SaleDetails();
        mockSaleDetails2.setId(600L);
        mockSaleDetails2.setProduct(mockProduct2);
        mockSaleDetails2.setQuantity(2);
        mockSaleDetails2.setSubtotal(4.00);

        Sale mockSale2 = new Sale();
        mockSale2.setId(11L);
        mockSale2.setDate(LocalDateTime.now());
        mockSale2.setBranch(mockBranch);
        mockSale2.setSaleDetails(List.of(mockSaleDetails2));
        mockSale2.setTotal(4.00);
        mockSaleDetails2.setSale(mockSale2);

        List<Sale> mockSales = new ArrayList<>();
        mockSales.add(mockSale);
        mockSales.add(mockSale2);

        Page<Sale> mockPage = new PageImpl<>(mockSales);

        when(saleRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<SaleResponseDTO> responseDTOList = saleService.findAll(Pageable.unpaged());

        assertNotNull(responseDTOList, "Response should not be null");
        assertFalse(responseDTOList.isEmpty(), "Response should not be empty");

        assertEquals(2, responseDTOList.getTotalElements(), "Total elements should match");
        assertEquals(2, responseDTOList.getNumberOfElements(), "Number of elements should match");

        assertEquals(10L, responseDTOList.getContent().get(0).getId(), "Sale ID should match");
        assertEquals(11L, responseDTOList.getContent().get(1).getId(), "Sale ID should match");

        assertEquals(7.50, responseDTOList.getContent().get(0).getTotal(), "Total should match and be perfectly calculated");
        assertEquals(4.00, responseDTOList.getContent().get(1).getTotal(), "Total should match and be perfectly calculated");

        assertEquals(1, responseDTOList.getContent().get(0).getSaleDetails().size(), "Sale details should have 1 item");
        assertEquals(1, responseDTOList.getContent().get(1).getSaleDetails().size(), "Sale details should have 1 item");

        assertEquals("Dr Pepper", responseDTOList.getContent().get(0).getSaleDetails().get(0).getProductName(), "Product name should be mapped");
        assertEquals("Milk", responseDTOList.getContent().get(1).getSaleDetails().get(0).getProductName(), "Product name should be mapped");

        assertEquals(500L, responseDTOList.getContent().get(0).getSaleDetails().get(0).getId(), "Sale detail ID should match");
        assertEquals(600L, responseDTOList.getContent().get(1).getSaleDetails().get(0).getId(), "Sale detail ID should match");
    }

    @Test
    @DisplayName("Should successfully return all sales when branch ID is correct")
    void findByBranchId() {
        Page<Sale> mockPage = new PageImpl<>(List.of(mockSale));

        when(saleRepository.findSalesByBranchId(eq(1L), any(Pageable.class))).thenReturn(mockPage);

        Page<SaleResponseDTO> responseDTOList = saleService.findByBranchId(1L, Pageable.unpaged());

        assertNotNull(responseDTOList, "Response should not be null");
        assertFalse(responseDTOList.isEmpty(), "Response should not be empty");

        assertEquals(1, responseDTOList.getTotalElements(), "Total elements should match");
        assertEquals(1, responseDTOList.getNumberOfElements(), "Number of elements should match");
        assertEquals(10L, responseDTOList.getContent().get(0).getId(), "Sale ID should match");
        assertEquals(7.50, responseDTOList.getContent().get(0).getTotal(), "Total should match and be perfectly calculated");
        assertFalse(responseDTOList.getContent().get(0).getSaleDetails().isEmpty(), "Sale details should not be empty");
        assertEquals("Dr Pepper", responseDTOList.getContent().get(0).getSaleDetails().get(0).getProductName(),
                "Number of elements should match");
        assertEquals(1L, responseDTOList.getContent().get(0).getBranchId(), "Branch ID should match");
    }

    @Test
    @DisplayName("Should logically cancel a sale and restore product stock to the inventory")
    void delete() {
        when(saleRepository.findById(10L)).thenReturn(Optional.of(mockSale));

        saleService.delete(10L);

        assertEquals(105, mockProduct.getStock(), "Stock should be incremented by 5");

        assertEquals(Status.CANCELLED, mockSale.getStatus(), "Status should be set to CANCELLED");

        verify(saleRepository, never()).delete(any(Sale.class));
        verify(productRepository, never()).deleteById(anyLong());

        verify(saleRepository, times(1)).findById(10L);
    }
}