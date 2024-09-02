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

        // we place the transaction (without OCC for now) >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        transaction.setSender(senderAccount);
        transaction.setReceiver(receiverAccount);
        transaction.setSenderAccountId(senderAccount.getAccountId());
        transaction.setReceiverAccountId(receiverAccount.getAccountId());

        senderAccount.setBalance(senderAccount.getBalance() - transaction.getAmount());
        receiverAccount.setBalance(receiverAccount.getBalance() + transaction.getAmount());

        // TODO >>>>>>>>>>>>>>>>>> check that the transaction appears in the .../sent and .../received arrays, otherwise we gotta set them manually!
        transactionRepository.save(transaction);
    }
}
