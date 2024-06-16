package com.mdp.next.service;

import java.util.List;
import java.util.ArrayList;
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

        // the readset and writeset of a transaction will always only contain the transaction's sender and receiver 
        // accounts, so there is no need to explicitly define this field

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

        // define the readset and writeset for transaction T
        List<Account> T_readSet = new ArrayList<>();
        T_readSet.add(t.getSender());
        T_readSet.add(t.getReceiver());

        List<Account> T_writeSet = new ArrayList<>();
        T_writeSet.add(t.getSender());
        T_writeSet.add(t.getReceiver());

        for (Transaction u : relevantSet) {

            // compute the readset and writeset for transaction U
            List<Account> U_readSet = new ArrayList<>();
            U_readSet.add(u.getSender());
            U_readSet.add(u.getReceiver());

            List<Account> U_writeSet = new ArrayList<>();
            U_writeSet.add(u.getSender());
            U_writeSet.add(u.getReceiver());

            boolean dirtyRead = T_writeSet.stream().noneMatch(U_readSet::contains);
            boolean lostUpdate = T_writeSet.stream().noneMatch(U_writeSet::contains);

            if (dirtyRead || lostUpdate) {
                // conflict was found, abort the youngest transaction
                // when u is the youngest transaction, nothing happens, and we assume an implicit abort
                // when t is the youngest, we exit out of this method by throwing an exception
                if (!u.isYoungerThan(t)) {
                    throw new ApiRuntimeException("The transaction could not be placed due to concurrency issues");
                } 
            }

        }

        // ================================================ COMMIT PHASE ==========================================

        t.setCurrPhase("COMMITTED");

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
