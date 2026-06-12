package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.BranchRequestDTO;
import com.fireboxsys.supermarkets.dto.BranchResponseDTO;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.model.Branch;
import com.fireboxsys.supermarkets.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fireboxsys.supermarkets.mappers.Mapper.toDTO;

@Service
public class BranchService implements IBranchService{

    private final BranchRepository branchRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public BranchResponseDTO save(BranchRequestDTO newBranchDTO) {
        Branch b = new Branch();
        b.setName(newBranchDTO.getName());
        b.setAddress(newBranchDTO.getAddress());
        return toDTO(branchRepository.save(b));
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponseDTO findById(Long id) {
        return branchRepository.findById(id).map(b -> toDTO(b)).orElseThrow(() ->
                new NotFoundException("Branch not found: id #" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponseDTO> findAll() {
        return branchRepository.findAll().stream().map(b -> toDTO(b)).toList();
    }

    @Override
    @Transactional
    public BranchResponseDTO update(Long id, BranchRequestDTO branchDTO) {
        Branch b = branchRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Branch not found: id #" + id));
        b.setName(branchDTO.getName());
        b.setAddress(branchDTO.getAddress());
        return toDTO(branchRepository.save(b));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Branch b = branchRepository.findById(id). orElseThrow(() ->
                new NotFoundException("Branch not found: id #" + id));
        branchRepository.delete(b);
    }
}
