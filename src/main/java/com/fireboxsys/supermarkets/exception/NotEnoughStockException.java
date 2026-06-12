package com.fireboxsys.supermarkets.exception;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException(String message) {
        super(message);
    }
}
