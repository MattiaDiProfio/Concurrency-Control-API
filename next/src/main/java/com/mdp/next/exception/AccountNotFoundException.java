package com.mdp.next.exception;

public class AccountNotFoundException extends RuntimeException { 
    public AccountNotFoundException(Long ID) { 
        super("The user with id '" + ID + "' does not have an active account");
    }
}