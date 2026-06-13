package com.fireboxsys.supermarkets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fireboxsys.supermarkets.dto.BranchRequestDTO;
import com.fireboxsys.supermarkets.dto.BranchResponseDTO;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.service.BranchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BranchController.class)
class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BranchService branchService;

    private BranchRequestDTO validRequestDTO;
    private BranchResponseDTO mockResponseDTO;
    private ObjectMapper objectMapper;
    private static final String DEFAULT_404_MESSAGE = "Branch not found with id: #";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        validRequestDTO = new BranchRequestDTO();
        validRequestDTO.setName("Main Branch");
        validRequestDTO.setAddress("123 Main St");

        mockResponseDTO = new BranchResponseDTO();
        mockResponseDTO.setId(1L);
        mockResponseDTO.setName("Main Branch");
        mockResponseDTO.setAddress("123 Main St");
    }

    // ------------------------------------------- HAPPY PATHS -----------------------------------------------------//

    @Test
    void findAllBranches() throws Exception{
        BranchResponseDTO mockResponseDTO2 = new BranchResponseDTO();
        mockResponseDTO2.setId(2L);
        mockResponseDTO2.setName("South Branch");
        mockResponseDTO2.setAddress("456 South St");

        List<BranchResponseDTO> mockBranches = List.of(mockResponseDTO, mockResponseDTO2);

        when(branchService.findAll()).thenReturn(mockBranches);

        mockMvc.perform(get("/api/branches"))
                .andExpect(status().isOk());
    }

    @Test
    void findBranchById() throws Exception{
        when(branchService.findById(1L)).thenReturn(mockResponseDTO);

        mockMvc.perform(get("/api/branches/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createBranch() throws Exception{
        when(branchService.save(any(BranchRequestDTO.class))).thenReturn(mockResponseDTO);

        mockMvc.perform(post("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Main Branch"))
                .andExpect(jsonPath("$.address").value("123 Main St"));
    }

    @Test
    void updateBranch() throws Exception{
        BranchRequestDTO updatedRequestDTO = new BranchRequestDTO();

        updatedRequestDTO.setName("Updated Branch");
        updatedRequestDTO.setAddress("456 Updated St");

        BranchResponseDTO updatedResponseDTO = new BranchResponseDTO();
        updatedResponseDTO.setId(1L);
        updatedResponseDTO.setName(updatedRequestDTO.getName());
        updatedResponseDTO.setAddress(updatedRequestDTO.getAddress());

        when(branchService.update(eq(1L), any(BranchRequestDTO.class))).thenReturn(updatedResponseDTO);

        mockMvc.perform(put("/api/branches/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Branch"))
                .andExpect(jsonPath("$.address").value("456 Updated St"));
    }

    @Test
    void deleteBranch() throws Exception{
        doNothing().when(branchService).delete(1L);

        mockMvc.perform(delete("/api/branches/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    // ------------------------------------------- SAD PATHS -----------------------------------------------------//

    @Test
    void findBranchById_whenBranchNotFound_shouldReturn404() throws Exception{
        when(branchService.findById(5L)).thenThrow(new NotFoundException(DEFAULT_404_MESSAGE + 5L));

        mockMvc.perform(get("/api/branches/{id}", 5L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBranch_whenInvalidData_shouldReturn400() throws Exception{
        BranchRequestDTO invalidRequestDTO = new BranchRequestDTO();
        invalidRequestDTO.setName("");
        invalidRequestDTO.setAddress("");

        mockMvc.perform(post("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBranch_whenBranchNotFound_shouldReturn404() throws Exception{
        BranchRequestDTO updatedRequestDTO = new BranchRequestDTO();
        updatedRequestDTO.setName("Updated Branch");
        updatedRequestDTO.setAddress("456 Updated St");

        when(branchService.update(eq(9L), any(BranchRequestDTO.class))).thenThrow(new NotFoundException(DEFAULT_404_MESSAGE + 9L));

        mockMvc.perform(put("/api/branches/{id}", 9L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBranch_whenInvalidData_shouldReturn400() throws Exception{
        BranchRequestDTO invalidRequestDTO = new BranchRequestDTO();
        invalidRequestDTO.setName("");
        invalidRequestDTO.setAddress("");

        mockMvc.perform(put("/api/branches/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBranch_whenBranchNotFound_shouldReturn404() throws Exception{
        doThrow(new NotFoundException(DEFAULT_404_MESSAGE + 5L)).when(branchService).delete(5L);

        mockMvc.perform(delete("/api/branches/{id}", 5L))
                .andExpect(status().isNotFound());
    }

    //-------------------------------------------- EDGE CASES -----------------------------------------------------//

    @Test
    void findBranchById_whenIdIsLetter_shouldReturn400() throws Exception{
        mockMvc.perform(get("/api/branches/{id}", "a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBranch_withMalformedJson_shouldReturn400() throws Exception{
        String malformedJson = "{ \"name\": \"Main Branch\", \"address\": \"123 Main St\" ";

    mockMvc.perform(post("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

}


