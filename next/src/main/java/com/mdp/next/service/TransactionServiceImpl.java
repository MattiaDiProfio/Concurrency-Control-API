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
        Long senderID = transaction.getSender().getID();
        Long receiverID = transaction.getReceiver().getID();

        Account sender = AccountServiceImpl.unwrapAccount(accountRepository.findById(senderID), senderID);
        Account receiver = AccountServiceImpl.unwrapAccount(accountRepository.findById(receiverID), receiverID);
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        // append the transaction to the relative lists
        List<Transaction> senderSent = sender.getSent();
        senderSent.add(transaction);
        sender.setSent(senderSent);

        List<Transaction> receiverReceived = receiver.getReceived();
        receiverReceived.add(transaction);
        receiver.setReceived(receiverReceived);

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
