package com.mdp.next.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long transactionID) {
        super("The transaction with id '" + transactionID + "' was not found");
    }
}
