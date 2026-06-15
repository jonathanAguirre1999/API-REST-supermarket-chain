package com.fireboxsys.supermarkets.controller;

import com.fireboxsys.supermarkets.dto.ProductRequestDTO;
import com.fireboxsys.supermarkets.dto.ProductResponseDTO;
import com.fireboxsys.supermarkets.service.IProductService;
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
@RequestMapping("/api/products")
public class ProductController {

    private final IProductService productService;

    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    //GET METHODS
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findAllProducts(
            @PageableDefault(page = 0, size = 50, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/top-product")
    public ResponseEntity<ProductResponseDTO> findTopProduct() {
        return ResponseEntity.ok(productService.findTopProduct());
    }

    //POST METHODS
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createNewProduct(@Valid @RequestBody ProductRequestDTO newProductDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(newProductDTO));
    }

    //PUT METHODS
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequestDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    //DELETE METHODS
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
