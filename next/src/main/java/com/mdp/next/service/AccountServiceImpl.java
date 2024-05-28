package com.mdp.next.service;

import java.util.List;
import java.util.Optional;
import com.mdp.next.entity.Account;
import com.mdp.next.exception.AccountNotFoundException;

public class AccountServiceImpl implements AccountService {

    @Override
    public List<Account> getAccounts() {
        return null;
    }

    @Override
    public Account getAccount(Long userID) {
        return null;
    }

    @Override
    public Account openAccount(Account account, Long userID) {
        return null;
    }

    @Override
    public Account updateAccount(Account account, Long userID) {
        return null;
    }

    @Override
    public void closeAccount(Long userID) {

    }

    public static Account unwrapAccount(Optional<Account> entity, Long userID) {
        if (entity.isPresent()) return entity.get();
        else throw new AccountNotFoundException(userID, "account");
    }
    
}
