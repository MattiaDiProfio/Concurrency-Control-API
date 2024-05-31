package com.mdp.next.exception;

public class InsufficientAssetsException extends RuntimeException {
    public InsufficientAssetsException(Long senderID) {
        super("The account with id '" + senderID + "' does not have enough assets to cover the transaction");
    }
}
