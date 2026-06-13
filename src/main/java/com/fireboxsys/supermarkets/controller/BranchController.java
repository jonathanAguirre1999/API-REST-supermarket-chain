package com.fireboxsys.supermarkets.controller;

import com.fireboxsys.supermarkets.dto.BranchRequestDTO;
import com.fireboxsys.supermarkets.dto.BranchResponseDTO;
import com.fireboxsys.supermarkets.service.BranchService;
import com.fireboxsys.supermarkets.service.IBranchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final IBranchService branchService;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    //GET METHODS
    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> findAllBranches() {
        return ResponseEntity.ok(branchService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> findBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.findById(id));
    }

    //POST METHODS
    @PostMapping
    public ResponseEntity<BranchResponseDTO> createBranch(@RequestBody @Valid BranchRequestDTO newBranchDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(branchService.save(newBranchDTO));
    }

    //PUT METHODS
    @PutMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> updateBranch(@PathVariable Long id, @RequestBody @Valid BranchRequestDTO branchDTO) {
        return ResponseEntity.ok(branchService.update(id, branchDTO));
    }

    //DELETE METHODS
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
