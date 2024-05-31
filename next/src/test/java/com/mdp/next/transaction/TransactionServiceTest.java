package com.mdp.next.transaction;

import com.mdp.next.entity.*;
import com.mdp.next.repository.*;
import com.mdp.next.service.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {
    
    @Mock
    AccountServiceImpl accountService;

    @Mock
    TransactionRepository transactionRepository;  

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Test
    public void testGetTransactions() {
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(new Transaction(25.00, 1L, 2L), new Transaction(10.00, 2L, 1L)));
        List<Transaction> allTransactions = transactionService.getTransactions();
        assertEquals(allTransactions.size(), 2);
        assertEquals(allTransactions.get(0).getAmount(), 25.00);
        assertEquals(allTransactions.get(0).getSenderID(), 2L);
        assertEquals(allTransactions.get(1).getReceiverID(), 2L);
    }

    @Test
    public void testGetAccountSentTransactions() {       
        Account account = new Account(100.00);
        account.setID(1L);
        when(accountService.getAccount(1L)).thenReturn(account);
        List<Transaction> sentTransactions = transactionService.getAccountSentTransactions(1L);
        assertEquals(sentTransactions, account.getSent());
    }

    @Test
    public void testGetAccountReceivedTransactions() {
        Account account = new Account(100.00);
        account.setID(1L);
        when(accountService.getAccount(1L)).thenReturn(account);
        List<Transaction> receivedTransactions = transactionService.getAccountReceivedTransactions(1L);
        assertEquals(receivedTransactions, account.getReceived());
    }

    @Test
    public void testPlaceTransaction() {
        when(accountService.getAccount(1L)).thenReturn(new Account(100.00));
        when(accountService.getAccount(2L)).thenReturn(new Account(25.00));

        Transaction transaction = new Transaction(10.00, 1L, 2L);
        transaction.setSender(accountService.getAccount(1L));
        transaction.setReceiver(accountService.getAccount(2L));

        transactionService.placeTransaction(transaction);
        assertEquals(accountService.getAccount(1L).getBalance(), 110.00);
        assertEquals(accountService.getAccount(2L).getBalance(), 15.00);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test 
    public void testAbortTransaction() {
        when(accountService.getAccount(1L)).thenReturn(new Account(100.00));
        when(accountService.getAccount(2L)).thenReturn(new Account(25.00));

        Transaction transaction = new Transaction(10.00, 1L, 2L);
        transaction.setID(1L);
        transaction.setSender(accountService.getAccount(1L));
        transaction.setReceiver(accountService.getAccount(2L));

        transactionService.placeTransaction(transaction);
        assertEquals(accountService.getAccount(1L).getBalance(), 110.00);
        assertEquals(accountService.getAccount(2L).getBalance(), 15.00);

        when(transactionRepository.findById(1L)).thenReturn(Optional.ofNullable(transaction));

        transactionService.abortTransaction(1L);
        
        verify(transactionRepository, times(1)).deleteById(1L);
        assertEquals(accountService.getAccount(1L).getBalance(), 100.00);
        assertEquals(accountService.getAccount(2L).getBalance(), 25.00);
    }

}
