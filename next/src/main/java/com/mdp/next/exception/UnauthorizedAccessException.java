package com.mdp.next.exception;

public class UnauthorizedAccessException extends RuntimeException { 
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}