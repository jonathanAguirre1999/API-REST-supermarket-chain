package com.fireboxsys.supermarkets.controller;

import com.fireboxsys.supermarkets.dto.SaleRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleResponseDTO;
import com.fireboxsys.supermarkets.service.ISaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final ISaleService saleService;

    @Autowired
    public SaleController(ISaleService saleService) {
        this.saleService = saleService;
    }

    //GET METHODS
    @GetMapping
    public ResponseEntity<Page<SaleResponseDTO>> findAllSales(
            @PageableDefault(page = 0, size = 50, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(saleService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> findSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findById(id));
    }

    @GetMapping("/branch/{id}")
    public ResponseEntity<Page<SaleResponseDTO>> findSalesByBranchId(@PathVariable Long id,
            @PageableDefault(page = 0, size = 50, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(saleService.findByBranchId(id, pageable));
    }

    //POST METHODS
    @PostMapping
    public ResponseEntity<SaleResponseDTO> registerSale(@RequestBody @Valid SaleRequestDTO newSaleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saleService.save(newSaleDTO));
    }

    //PUT METHODS ARE NOT IMPLEMENTED BECAUSE SALES CANNOT BE UPDATED

    //DELETE METHODS
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
