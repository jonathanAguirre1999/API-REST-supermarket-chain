package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.BranchRequestDTO;
import com.fireboxsys.supermarkets.dto.BranchResponseDTO;

import java.util.List;

public interface IBranchService {

    BranchResponseDTO save(BranchRequestDTO newBranchDTO);
    BranchResponseDTO findById(Long id);
    List<BranchResponseDTO> findAll();
    BranchResponseDTO update(Long id, BranchRequestDTO branchDTO);
    void delete(Long id);

}
