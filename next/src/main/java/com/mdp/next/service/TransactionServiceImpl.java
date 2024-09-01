package com.mdp.next.service;

import com.mdp.next.entity.Account;
import com.mdp.next.entity.Transaction;
import com.mdp.next.repository.AccountRepository;
import com.mdp.next.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<Transaction> getSentTransactions(Long userID, Long accountID) {
        // if the account ID is not valid, this call to the accountRepository will catch it
        Account account = AccountServiceImpl.unwrapAccount(accountRepository.findById(accountID), userID, accountID);
        return account.getSentTransactions();
    }

    @Override
    public List<Transaction> getReceivedTransactions(Long userID, Long accountID) {
        // if the account ID is not valid, this call to the accountRepository will catch it
        Account account = AccountServiceImpl.unwrapAccount(accountRepository.findById(accountID), userID, accountID);
        return account.getReceivedTransactions();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    @Override
    public void placeTransaction(Transaction transaction) {

    }
}
