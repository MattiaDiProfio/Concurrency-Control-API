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

    AccountServiceImpl accountService;
    TransactionRepository transactionRepository;    

    @Override
    public List<Transaction> getTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    @Override   
    public List<Transaction> getAccountSentTransactions(Long userID) {
        Account account = accountService.getAccount(userID);
        return account.getSent();
    } 

    @Override 
    public List<Transaction> getAccountReceivedTransactions(Long userID) {
        Account account = accountService.getAccount(userID);
        return account.getReceived();
    }

    @Override   
    public Transaction placeTransaction(Transaction transaction) {

        Double amount = transaction.getAmount();

        // check that the amount is positive and non-zero
        if (amount <= 0.00) throw new NonPositiveAmountException();

        Long senderID = transaction.getSenderID();
        Long receiverID = transaction.getReceiverID();

        // ensure that the sender and receiverID are valid (i.e. not equal)
        if (senderID.equals(receiverID)) throw new AccountsMustDifferException();

        // fetch the accounts based on the transaction's receiverID and senderID
        Account sender = accountService.getAccount(senderID);
        Account receiver = accountService.getAccount(receiverID);

        // ensure that the sender has enough assets to cover the transaction amount
        if (sender.getBalance() < amount) throw new InsufficientAssetsException(senderID);

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
 
    public static Transaction unwrapTransaction(Optional<Transaction> entity, Long transactionID) {
        if (entity.isPresent()) return entity.get();
        else throw new TransactionNotFoundException(transactionID);
    }

    public Transaction getTransactionById(Long transactionID) {
        return unwrapTransaction(transactionRepository.findById(transactionID), transactionID);
    }
}
