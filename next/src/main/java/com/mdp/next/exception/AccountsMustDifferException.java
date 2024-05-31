package com.mdp.next.exception;

public class AccountsMustDifferException extends RuntimeException {
    public AccountsMustDifferException() {
        super("Sender account and Receiver account cannot be the same");
    }
}
