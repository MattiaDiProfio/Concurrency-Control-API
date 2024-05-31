package com.mdp.next.service;

import java.util.List;
import com.mdp.next.entity.Transaction;

public interface TransactionService {
    List<Transaction> getTransactions();
    List<Transaction> getAccountSentTransactions(Long userID);
    List<Transaction> getAccountReceivedTransactions(Long userID);
    Transaction placeTransaction(Transaction transaction);
    void abortTransaction(Long transactionID);
    Transaction getTransactionById(Long transactionID);
}
