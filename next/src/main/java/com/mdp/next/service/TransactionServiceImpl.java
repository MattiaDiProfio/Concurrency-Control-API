package com.mdp.next.service;

import com.mdp.next.entity.Account;
import com.mdp.next.entity.Transaction;
import com.mdp.next.entity.User;
import com.mdp.next.repository.AccountRepository;
import com.mdp.next.repository.TransactionRepository;
import com.mdp.next.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public List<Transaction> getSentTransactions(Long userID, Long accountID) {
        // we attempt to get the user object to check the validity of the userID
        User user = UserServiceImpl.unwrapUser(userRepository.findById(userID), userID);
        // if the account ID is not valid, this call to the accountRepository will catch it
        Account account = AccountServiceImpl.unwrapAccount(accountRepository.findById(accountID), userID, accountID);
        return account.getSentTransactions();
    }

    @Override
    public List<Transaction> getReceivedTransactions(Long userID, Long accountID) {
        // we attempt to get the user object to check the validity of the userID
        User user = UserServiceImpl.unwrapUser(userRepository.findById(userID), userID);
        // if the account ID is not valid, this call to the accountRepository will catch it
        Account account = AccountServiceImpl.unwrapAccount(accountRepository.findById(accountID), userID, accountID);
        return account.getReceivedTransactions();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    @Override
    public void placeTransaction(Transaction transaction, Long userID, Long accountID) {

        // extract the amount and accounts involved in the transaction

        // check the validity of the transaction
        // we can do the following checks in the service layer!
        // namely, the amount specified is > 0.0
        // the account and sender cannot be the same, and they must both be valid (existing)
        // the sender account must have enough funds to cover the transaction
        // in the service layer, trigger the OCC algorithm

    }
}
