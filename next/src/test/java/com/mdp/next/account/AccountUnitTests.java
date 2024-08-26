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

import java.util.Optional;

import static org.mockito.Mockito.when;

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

    }

    @Test
    public void testGetAllAccountsByUserId() {

    }

    @Test
    public void testGetAllAccounts() {

    }

    @Test
    public void testOpenAccount() {

    }

    @Test
    public void testCloseAccount() {

    }

}
