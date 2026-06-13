package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.BranchRequestDTO;
import com.fireboxsys.supermarkets.dto.BranchResponseDTO;
import com.fireboxsys.supermarkets.model.Branch;
import com.fireboxsys.supermarkets.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchService branchService;

    //MOCK OBJECTS
    private Branch mockBranch;
    private BranchRequestDTO validRequestDTO;

    @BeforeEach
    void setUp() {
        mockBranch = new Branch();
        mockBranch.setId(1L);
        mockBranch.setName("Main Branch");
        mockBranch.setAddress("123 Main St");

        validRequestDTO = new BranchRequestDTO();
        validRequestDTO.setName("Main Branch");
        validRequestDTO.setAddress("123 Main St");

    }

    // ------------------------------------------- HAPPY PATHS -----------------------------------------------------//

    @Test
    @DisplayName("Should successfully save a new branch")
    void save() {
        when(branchRepository.save(any(Branch.class))).thenReturn(mockBranch);

        BranchResponseDTO responseDTO = branchService.save(validRequestDTO);

        assertNotNull(responseDTO, "Branch should not be null");

        assertEquals(1L, responseDTO.getId(), "ID should be set to 1");
        assertEquals("Main Branch", responseDTO.getName(), "Name should be set to Main Branch");
        assertEquals("123 Main St", responseDTO.getAddress(), "Address should be set to 123 Main St");
    }

    @Test
    @DisplayName("Should successfully return a branch when provided ID is correct")
    void findById() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));

        BranchResponseDTO responseDTO = branchService.findById(1L);

        assertNotNull(responseDTO, "Branch should not be null");
        assertEquals(1L, responseDTO.getId(), "ID should be set to 1");
        assertEquals("Main Branch", responseDTO.getName(), "Name should be set to Main Branch");
        assertEquals("123 Main St", responseDTO.getAddress(), "Address should be set to 123 Main St");
    }

    @Test
    @DisplayName("Should successfully return all branches")
    void findAll() {
        Branch mockBranch2 = new Branch();

        mockBranch2.setId(2L);
        mockBranch2.setName("South Branch");
        mockBranch2.setAddress("456 Branch St");

        List<Branch> mockBranches = new ArrayList<>();
        mockBranches.add(mockBranch);
        mockBranches.add(mockBranch2);

        when(branchRepository.findAll()).thenReturn(mockBranches);

        List<BranchResponseDTO> responseDTOList = branchService.findAll();

        assertNotNull(responseDTOList, "Response should not be null");
        assertFalse(responseDTOList.isEmpty(), "Response should not be empty");
        assertEquals(2, responseDTOList.size(), "Response should contain 2 branches");

        assertEquals(1L, responseDTOList.get(0).getId(), "ID should be set to 1");
        assertEquals("Main Branch", responseDTOList.get(0).getName(), "Name should be set to Main Branch");
        assertEquals("123 Main St", responseDTOList.get(0).getAddress(), "Address should be set to 123 Main St");

        assertEquals(2L, responseDTOList.get(1).getId(), "ID should be set to 2");
        assertEquals("South Branch", responseDTOList.get(1).getName(), "Name should be set to South Branch");
        assertEquals("456 Branch St", responseDTOList.get(1).getAddress(), "Address should be set to 456 Branch St");
    }

    @Test
    @DisplayName("Should successfully update a branch, ID shouldn't be modified")
    void update() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));
        when(branchRepository.save(any(Branch.class))).thenReturn(mockBranch);

        BranchRequestDTO mockUpdatedBranch = new BranchRequestDTO();
        mockUpdatedBranch.setName("Updated Branch");
        mockUpdatedBranch.setAddress("Updated Address");

        BranchResponseDTO responseDTO = branchService.update(1L, mockUpdatedBranch);

        assertNotNull(responseDTO, "Branch should not be null");
        assertEquals(1L, responseDTO.getId(), "ID should be the same as the original");
        assertEquals("Updated Branch", responseDTO.getName(), "Name should be set to Updated Branch");
        assertEquals("Updated Address", responseDTO.getAddress(), "Address should be set to Updated Address");
    }

    @Test
    @DisplayName("Should logically delete a branch")
    void delete() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));

        branchService.delete(1L);

        verify(branchRepository, times(1)).delete(mockBranch);;
    }
}