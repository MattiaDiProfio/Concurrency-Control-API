package com.mdp.next.exception;

public class UserAccountUniquenessViolationException extends RuntimeException { 
    public UserAccountUniquenessViolationException(Long ID) { 
        super("The user with id '" + ID + "' already has an active account");
    }
}