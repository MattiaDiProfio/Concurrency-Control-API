package com.mdp.next.account;

import com.mdp.next.entity.Account;
import com.mdp.next.entity.User;
import com.mdp.next.repository.AccountRepository;
import com.mdp.next.repository.UserRepository;
import com.mdp.next.service.AccountServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AccountUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void testGetAccountByUserIdAndAccountId() {
        // instantiate a user and an account
        Account a = new Account("CHECKING");
        User u = new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "CUSTOMER");

        // configure the relationship between the user and the account
        u.setUserId(1L);
        a.setAccountId(1L);
        a.setAccountOwner(u);
        a.setAccountOwnerId(u.getUserId());
        u.setAccounts(List.of(a));

        // ensure that the accountService returns the expected account given a user id and account id
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        Account fetchedAccount = accountService.getAccount(1L, 1L);
        assertEquals(fetchedAccount, a);
    }

    @Test
    public void testGetAllAccountsByUserId() {
        // instantiate a user and 3 accounts
        Account checking = new Account("CHECKING");
        Account savings = new Account("SAVINGS");
        Account investment = new Account("INVESTMENT");
        User u = new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "CUSTOMER");

        // configure the relationship between the user and the account
        u.setUserId(1L);
        checking.setAccountId(1L);
        checking.setAccountOwner(u);
        checking.setAccountOwnerId(u.getUserId());

        checking.setAccountId(2L);
        checking.setAccountOwner(u);
        checking.setAccountOwnerId(u.getUserId());

        checking.setAccountId(3L);
        checking.setAccountOwner(u);
        checking.setAccountOwnerId(u.getUserId());

        u.setAccounts(List.of(checking, savings, investment));

        // ensure that the accountService returns all accounts linked to the user with id of 1
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        List<Account> userAccounts = accountService.getUserAccounts(1L);
        assertEquals(userAccounts.size(), 3);
        assertTrue(userAccounts.contains(checking));
        assertTrue(userAccounts.contains(savings));
        assertTrue(userAccounts.contains(investment));
    }

    @Test
    public void testGetAllAccounts() {
        Account a = new Account("SAVINGS");
        Account b = new Account("SAVINGS");
        Account c = new Account("INVESTMENT");
        Account d = new Account("CHECKING");

        when(accountRepository.findAll()).thenReturn(List.of(a,b,c,d));
        List<Account> accounts = accountService.getAllAccounts();

        assertEquals(accounts.size(), 4);
        assertTrue(accounts.contains(a));
        assertTrue(accounts.contains(b));
        assertTrue(accounts.contains(c));
        assertTrue(accounts.contains(d));
    }

    @Test
    public void testOpenAccount() {
        User u = new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "CUSTOMER");
        u.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        Account a = new Account("SAVINGS");
        accountService.openAccount(a, 1L);

        verify(accountRepository, times(1)).save(a);
        assertEquals(a.getAccountOwner(), u);
        assertEquals(a.getAccountOwnerId(), u.getUserId());
        assertTrue(u.getAccounts().contains(a));
    }

    @Test
    public void testCloseAccount() {
        Account a = new Account("SAVINGS");
        User u = new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "CUSTOMER");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(a));
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        accountService.closeAccount(1L, 1L);

        verify(accountRepository, times(1)).delete(a);
    }

}
