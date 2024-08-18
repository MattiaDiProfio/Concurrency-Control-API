package com.mdp.next.exception;

public class UnauthorizedAccessException extends RuntimeException { 
    public UnauthorizedAccessException() { 
        super("Unauthorized access! You must be the resource owner or an admin to interact with this resource");
    }
}