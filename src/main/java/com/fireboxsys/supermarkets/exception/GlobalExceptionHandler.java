package com.fireboxsys.supermarkets.exception;

import com.fireboxsys.supermarkets.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotEnoughStockException(NotEnoughStockException ex, HttpServletRequest request) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request){
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred, please try again later or contact support.")
                .path(request.getRequestURI())
                .build();

        System.out.println(ex.getMessage());
        ex.printStackTrace();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation errors for one or more fields.")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }
}
