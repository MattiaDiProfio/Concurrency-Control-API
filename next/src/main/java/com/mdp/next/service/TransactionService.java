package com.mdp.next.service;

import com.mdp.next.entity.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getSentTransactions(Long userID, Long accountID);
    List<Transaction> getReceivedTransactions(Long userID, Long accountID);
    List<Transaction> getAllTransactions();
    void placeTransaction(Transaction transaction);

}
