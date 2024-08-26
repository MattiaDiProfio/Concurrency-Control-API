package com.mdp.next.exception;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(Long userId, String type) {
        super(String.format("The user with id %s already manages an account of type %s", userId, type));
    }
}
