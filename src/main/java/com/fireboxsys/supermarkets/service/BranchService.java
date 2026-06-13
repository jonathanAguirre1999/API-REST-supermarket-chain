package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.BranchRequestDTO;
import com.fireboxsys.supermarkets.dto.BranchResponseDTO;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.mappers.Mapper;
import com.fireboxsys.supermarkets.model.Branch;
import com.fireboxsys.supermarkets.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fireboxsys.supermarkets.mappers.Mapper.toDTO;

@Service
public class BranchService implements IBranchService{

    private static final String DEFAULT_MESSAGE = "Branch not found: id#";
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
        return branchRepository.findById(id).map(Mapper::toDTO).orElseThrow(() ->
                new NotFoundException(DEFAULT_MESSAGE + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponseDTO> findAll() {
        return branchRepository.findAll().stream().map(Mapper::toDTO).toList();
    }

    @Override
    @Transactional
    public BranchResponseDTO update(Long id, BranchRequestDTO branchDTO) {
        Branch b = branchRepository.findById(id).orElseThrow(() ->
                new NotFoundException(DEFAULT_MESSAGE + id));
        b.setName(branchDTO.getName());
        b.setAddress(branchDTO.getAddress());
        return toDTO(branchRepository.save(b));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Branch b = branchRepository.findById(id). orElseThrow(() ->
                new NotFoundException(DEFAULT_MESSAGE + id));
        branchRepository.delete(b);
    }
}
