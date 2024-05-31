package com.mdp.next.exception;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(Long transactionID, Long senderID, Long receiverID) {
        super(
            "The transaction with id '" + transactionID + "' involves a sender account with id '" 
            + senderID + "' and a receiver account with id '" + receiverID + "'. Make sure both are still active."
        );
    }
}
