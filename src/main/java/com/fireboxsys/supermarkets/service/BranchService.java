package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.BranchRequestDTO;
import com.fireboxsys.supermarkets.dto.BranchResponseDTO;
import com.fireboxsys.supermarkets.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService implements IBranchService{

    private final BranchRepository branchRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public BranchResponseDTO save(BranchRequestDTO newBranchDTO) {
        return null;
    }

    @Override
    public BranchResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public List<BranchResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public BranchResponseDTO update(Long id, BranchRequestDTO branchDTO) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
