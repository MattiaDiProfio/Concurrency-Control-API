package com.mdp.next.service;

import com.mdp.next.entity.Account;
import java.util.List;

public interface AccountService {

    Account getAccount(Long userId, Long accountId);
    List<Account> getUserAccounts(Long userId);
    List<Account> getAllAccounts();
    Account openAccount(Account account, Long userId);
    void closeAccount(Long userId, Long accountId);
}
