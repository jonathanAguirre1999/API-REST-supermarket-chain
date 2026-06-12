package com.fireboxsys.supermarkets.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private Integer statusCode;
    private String error;
    private String message;
    private String path;
    private List<String> validationErrors;
}
