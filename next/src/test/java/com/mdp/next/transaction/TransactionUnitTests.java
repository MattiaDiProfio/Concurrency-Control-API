package com.mdp.next.transaction;

import com.mdp.next.repository.AccountRepository;
import com.mdp.next.repository.TransactionRepository;
import com.mdp.next.service.AccountServiceImpl;
import com.mdp.next.service.TransactionServiceImpl;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionUnitTests {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    public void testComesBeforeMethod() {
        assertTrue(transactionService.comesBefore("2024-01-01 10:00:00", "2024-01-02 10:00:00"));
        assertFalse(transactionService.comesBefore(null, "2024-01-02 10:00:00"));
        assertFalse(transactionService.comesBefore("2024-01-01 10:00:00", null));
        assertFalse(transactionService.comesBefore("2024-01-01 10:00:00", "2024-01-01 09:59:59"));
        assertFalse(transactionService.comesBefore("2024-01-02 10:00:00", "2024-01-01 10:00:00"));
    }

}
