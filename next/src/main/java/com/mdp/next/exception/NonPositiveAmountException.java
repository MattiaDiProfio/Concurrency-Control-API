package com.mdp.next.exception;

public class NonPositiveAmountException extends RuntimeException {
    public NonPositiveAmountException(Long transactionID) {
        super("The transaction with id '" + transactionID + "' must have an amount greater than £0.00.");
    }
}
