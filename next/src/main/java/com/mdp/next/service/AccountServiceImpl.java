package com.mdp.next.service;

import com.mdp.next.entity.Account;
import com.mdp.next.entity.User;
import com.mdp.next.exception.AccountNotFoundException;
import com.mdp.next.exception.DuplicateAccountException;
import com.mdp.next.repository.UserRepository;
import com.mdp.next.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public Account getAccount(Long userId, Long accountId) {
        User user = UserServiceImpl.unwrapUser(userRepository.findById(userId), userId);
        for(Account account : user.getAccounts()) {
            if (account.getAccountId().equals(accountId)) return account;
        }
        throw new AccountNotFoundException(userId, accountId);
    }

    @Override
    public List<Account> getUserAccounts(Long userId) {
        User user = UserServiceImpl.unwrapUser(userRepository.findById(userId), userId);
        return user.getAccounts();
    }

    @Override
    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepository.findAll();
    }

    @Override
    public Account openAccount(Account account, Long userID) {
        User user = UserServiceImpl.unwrapUser(userRepository.findById(userID), userID);
        // ensure that an account with the same type does not exist already
        for(Account acc : user.getAccounts()) {
            if (acc.getType().equals(account.getType())) throw new DuplicateAccountException(userID, account.getType());
        }

        // link the user to this account
        account.setAccountOwner(user);
        account.setAccountOwnerId(user.getUserId());

        // add this new account to the list of accounts managed by the user
        List<Account> userAccounts = user.getAccounts();
        userAccounts.add(account);
        user.setAccounts(userAccounts);

        return accountRepository.save(account);
    }

    @Override
    public void closeAccount(Long userID, Long accountID) {
        // this retrieval ensures that the userID passed is valid, nothing else!
        UserServiceImpl.unwrapUser(userRepository.findById(userID), userID);
        Account account = unwrapAccount(accountRepository.findById(accountID), userID, accountID);
        accountRepository.delete(account);
    }

    public static Account unwrapAccount(Optional<Account> entity, Long userID, Long accountID) {
        if (entity.isPresent()) return entity.get();
        else throw new AccountNotFoundException(userID, accountID);
    }

}
