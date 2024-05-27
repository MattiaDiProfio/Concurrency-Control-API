package com.mdp.next.exception;

public class EntityNotFoundException extends RuntimeException { 
    public EntityNotFoundException(String ID, String entityName) { 
        super("The " + entityName + " with id '" + ID + "' does not exist in our records");
    }
}