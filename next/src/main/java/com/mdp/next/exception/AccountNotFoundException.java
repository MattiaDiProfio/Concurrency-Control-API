package com.mdp.next.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long userId, Long accountId) {
        super(String.format("The user with id %s does not have an account with id %s", userId, accountId));
    }
}
