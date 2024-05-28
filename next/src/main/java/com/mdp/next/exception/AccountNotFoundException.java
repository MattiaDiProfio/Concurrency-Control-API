package com.mdp.next.exception;

public class AccountNotFoundException extends RuntimeException { 
    public AccountNotFoundException(Long ID, String entityName) { 
        super("The " + entityName + " with id '" + ID + "' does not have an active account");
    }
}