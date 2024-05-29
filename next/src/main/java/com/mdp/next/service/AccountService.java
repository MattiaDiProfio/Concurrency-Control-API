package com.mdp.next.service;

import java.util.List;
import com.mdp.next.entity.Account;

public interface AccountService {
    public List<Account> getAccounts();
    public Account getAccount(Long userID);
    public Account openAccount(Account account, Long userID);
    public void closeAccount(Long userID);
}