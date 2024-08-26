package com.mdp.next.exception;

public class UserNotFoundException extends RuntimeException { 
    public UserNotFoundException(Long ID) {
        super("The user with id '" + ID + "' does not exist in our records");
    }
}