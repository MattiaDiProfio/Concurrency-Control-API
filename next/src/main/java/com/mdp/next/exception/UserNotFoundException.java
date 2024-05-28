package com.mdp.next.exception;

public class UserNotFoundException extends RuntimeException { 
    public UserNotFoundException(Long ID, String entityName) { 
        super("The " + entityName + " with id '" + ID + "' does not exist in our records");
    }
}