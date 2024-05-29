package com.mdp.next.service;

import java.util.List;
import java.util.Optional;
import com.mdp.next.entity.*;
import com.mdp.next.exception.*;
import com.mdp.next.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    AccountRepository accountRepository;
    TransactionRepository transactionRepository;    

    @Override
    public List<Transaction> getTransactions() {
        return null;
    }

    @Override   
    public List<Transaction> getAccountSentTransactions(Long userID) {
        return null;
    } 

    @Override 
    public List<Transaction> getAccountReceivedTransactions(Long userID) {
        return null;
    }

    @Override   
    public Transaction placeTransaction(Transaction transaction) {
        return null;
    }

    @Override   
    public void deleteTransaction(Long transactionID) {

    }

    @Override   
    public void abortTransaction(Long transactionID) {

    }

}
