package com.mdp.next.service;

import java.util.ArrayList;
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
    public Transaction placeTransaction(Transaction t) {

        Double amount = t.getAmount();

        // check that the amount is positive and non-zero
        if (amount <= 0.00) throw new NonPositiveAmountException();

        Long senderID = t.getSenderID();
        Long receiverID = t.getReceiverID();

        // ensure that the sender and receiverID are valid (i.e. not equal)
        if (senderID.equals(receiverID)) throw new AccountsMustDifferException();

        // ================================================ WORKING PHASE ==========================================

        // create tentative copies of the accounts the transaction will operate on
        Account sender = new Account(accountService.getAccount(senderID));
        Account receiver = new Account(accountService.getAccount(receiverID));

        // ensure that the sender has enough assets to cover the transaction amount
        if (sender.getBalance() < amount) throw new InsufficientAssetsException(senderID);

        // add the accounts to the read and write sets of the transaction
        t.addReadObject(sender);
        t.addReadObject(receiver);
        t.addWriteObject(sender);
        t.addWriteObject(receiver);
        transactionRepository.save(t);

        // execute the transfer of currency
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        // connect the accounts to the transaction 
        t.setSender(sender);
        t.setReceiver(receiver);

        // ================================================ VALIDATION PHASE ==========================================
        t.setCurrPhase("VALIDATION");

        // get all transactions who are in the working phase and were created before or at the same time as T
        List<Transaction> relevantSet = transactionRepository.getOverlappingTransactions(t.getCreatedAt());

        // check that for each transaction U in overlaps, U's readset doesn't overlap T's write set - dirty read
        // check that for each transaction U in overlaps, U's writeset doesn't overlap T's write set - lost update read
        for (Transaction u : relevantSet) {
            boolean dirtyRead = t.getWriteSet().stream().noneMatch(u.getReadSet()::contains);
            boolean lostUpdate = t.getWriteSet().stream().noneMatch(u.getWriteSet()::contains);

            if (dirtyRead || lostUpdate) {
                // conflict was found, abort the youngest transaction
                if (u.isYoungerThan(t)) {
                    u.emptyReadWriteSets();
                    transactionRepository.save(u);
                } else {
                    t.emptyReadWriteSets();
                    transactionRepository.save(t);
                    throw new ApiRuntimeException("The transaction could not be placed due to concurrency issues");
                }
            }

        }

        // ================================================ COMMIT PHASE ==========================================

        t.setCurrPhase("COMMITTED");

        // empty the read and write sets of t, since the transaction is in the commit phase
        t.emptyReadWriteSets();
               
        // update both the accounts' sent and received lists 
        sender.addSentTransaction(t);
        receiver.addReceivedTransaction(t);

        // set the accounts operated on by the transaction to their tentative copies 
        accountService.commitChanges(senderID, sender);
        accountService.commitChanges(receiverID, receiver);

        return transactionRepository.save(t);
    }
 
    public static Transaction unwrapTransaction(Optional<Transaction> entity, Long transactionID) {
        if (entity.isPresent()) return entity.get();
        else throw new TransactionNotFoundException(transactionID);
    }

    public Transaction getTransactionById(Long transactionID) {
        return unwrapTransaction(transactionRepository.findById(transactionID), transactionID);
    }
}
