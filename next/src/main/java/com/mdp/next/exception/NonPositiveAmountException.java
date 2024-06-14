package com.mdp.next.exception;

public class NonPositiveAmountException extends RuntimeException {
    public NonPositiveAmountException() {
        super("The transaction must have an amount greater than £0.00.");
    }
}
