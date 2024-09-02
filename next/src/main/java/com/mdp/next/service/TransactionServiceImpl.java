package com.mdp.next.service;

import com.mdp.next.entity.Account;
import com.mdp.next.entity.Transaction;
import com.mdp.next.entity.User;
import com.mdp.next.repository.AccountRepository;
import com.mdp.next.repository.TransactionRepository;
import com.mdp.next.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.mdp.next.exception.InvalidTransactionException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

        // fetching the sender & receiver accounts automatically checks for their existence as well
        Account senderAccount = AccountServiceImpl.unwrapAccount(accountRepository.findById(accountID), userID, accountID);
        Optional<Account> acc = accountRepository.findById(transaction.getReceiverAccountId());
        Account receiverAccount;
        if (acc.isPresent()) receiverAccount = acc.get();
        else throw new InvalidTransactionException(String.format("The receiver account with id %s does not exist in our records", transaction.getReceiverAccountId()));

        // ensure that the sender and receiver are not the same account
        if (senderAccount.getAccountId().equals(receiverAccount.getAccountId())) throw new InvalidTransactionException("Sender and Receiver accounts must be different");

        // ensure that the sender account has enough funds to cover the transaction
        if (senderAccount.getBalance() < transaction.getAmount()) throw new InvalidTransactionException("Insufficient balance to place transaction");

        // ensure that the amount is at least 0.01
        if (transaction.getAmount() <= 0.00) throw new InvalidTransactionException("Amount cannot be less than 0.01");

        // at this stage, the transaction payload is valid

        // OCC algorithm >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        // 1. assign the transaction start-timestamp
        transaction.setTransactionStartTimeStamp(timestampNow());

        // 2. define tentative copies of the write objects
        Account tentativeSender = new Account(senderAccount);
        Account tentativeReceiver = new Account(receiverAccount);

        // 3. define the Read and Write sets for this transaction
        transaction.setReadSet(List.of(senderAccount, receiverAccount));
        transaction.setWriteSet(List.of(tentativeSender, tentativeReceiver));

        // 4. carry out transaction's operations
        tentativeSender.setBalance(tentativeSender.getBalance() - transaction.getAmount());
        tentativeReceiver.setBalance(tentativeReceiver.getBalance() + transaction.getAmount());

        // 5. assign timestamp to mark the end of the read(working) phase
        transaction.setTransactionEndWorkingPhaseTimeStamp(timestampNow());

        // 6. assign validation ID - set it as the transactionID , since this guarantees uniqueness
        transaction.setValidationId(transaction.getTransactionId());

        // 7. carry out validation on current transaction, name it Tj
        List<Transaction> relevantSet = transactionRepository.getRelevantSet(transaction.getValidationId());
        boolean canCommit = true;

        // for each transaction in the relevant set, we check that either
        // (a) it ended before Tj begun
        // (b) there is no risk of lost-update (transactions overwriting each other)
        // (c) there is no risk of dirty-read (transactions basing calculations on outdated data)
        // if either a, b, or c are true for any given Ti, we can commit, otherwise we abort Tj
        for (Transaction Ti : relevantSet) {
            boolean noOverlap = false;
            boolean noDirtyRead = false;
            boolean noLostUpdate = false;

            // no point in checking, since the transactions don't overlap
            if (comesBefore(Ti.getTransactionEndTimeStamp(), transaction.getTransactionStartTimeStamp())) noOverlap = true;

            // check for dirty read
            if (Ti.getReadSet().stream().noneMatch(transaction.getReadSet()::contains)) noDirtyRead = true;

            // check for lost update
            List<Account> union = transaction.getWriteSet();
            union.addAll(transaction.getReadSet());
            if (
                Ti.getWriteSet().stream().noneMatch(union::contains) &&
                comesBefore(Ti.getTransactionEndWorkingPhaseTimeStamp(), transaction.getTransactionEndWorkingPhaseTimeStamp())
            ) noLostUpdate = true;

            if (!noOverlap || (!noDirtyRead || !noLostUpdate)) {
                // we must abort the transaction
                throw new InvalidTransactionException("We could not place the transaction, OCC failed");
            }
        }

        transaction.setTransactionEndValidationPhaseTimeStamp(timestampNow());

        // at this stage, the transaction is ok to be committed

        // 1. we make the changes to the accounts persistent
        senderAccount.updateTo(tentativeSender);
        receiverAccount.updateTo(tentativeReceiver);

        // 2. we link the transaction and the accounts involved
        transaction.setSender(senderAccount);
        transaction.setReceiver(receiverAccount);
        transaction.setSenderAccountId(senderAccount.getAccountId());
        transaction.setReceiverAccountId(receiverAccount.getAccountId());

        transaction.setTransactionEndTimeStamp(timestampNow());
        transactionRepository.save(transaction);
    }

    public String timestampNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public boolean comesBefore(String t1, String t2) {
        // if either string is null, we assume the timestamp to be in the future, so return true
        if (t1 == null || t2 == null) return true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime a = LocalDateTime.parse(t1, formatter);
        LocalDateTime b = LocalDateTime.parse(t2, formatter);
        return a.isBefore(b);
    }
}
