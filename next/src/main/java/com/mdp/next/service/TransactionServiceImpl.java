package com.mdp.next.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.mdp.next.entity.*;
import com.mdp.next.exception.*;
import com.mdp.next.repository.*;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    AccountRepository accountRepository;

    AccountServiceImpl accountService;
    TransactionRepository transactionRepository;    

    @Override
    public List<Transaction> getTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    @Override   
    public List<Transaction> getAccountSentTransactions(Long userID) {
        Account account = AccountServiceImpl.unwrapAccount(accountRepository.findByUserID(userID), userID);
        return account.getSent();
    } 

    @Override 
    public List<Transaction> getAccountReceivedTransactions(Long userID) {
        Account account = AccountServiceImpl.unwrapAccount(accountRepository.findByUserID(userID), userID);
        return account.getReceived();
    }

    @Override   
    public Transaction placeTransaction(Transaction transaction) {

        Double amount = transaction.getAmount();
        Long senderID = transaction.getSenderID();
        Long receiverID = transaction.getReceiverID();

        // fetch the accounts based on the transaction's receiverID and senderID
        Account sender = accountService.getAccount(senderID);
        Account receiver = accountService.getAccount(receiverID);

        // execute the transfer of currency
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        // update both the accounts' sent and received lists 
        sender.addSentTransaction(transaction);
        receiver.addReceivedTransaction(transaction);

        // connect the accounts to the transaction 
        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        // save the transaction to repository
        return transactionRepository.save(transaction);

    }

    @Override   
    public void deleteTransaction(Long transactionID) {

        // check if once a transaction is deleted from the repository, does it disappear from all the 
        // accounts' logs in which it appears?????
        transactionRepository.deleteById(transactionID);
    }

    @Override   
    public void abortTransaction(Long transactionID) {
        
    }

}
