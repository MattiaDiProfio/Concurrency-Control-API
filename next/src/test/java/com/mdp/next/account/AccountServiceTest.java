// package com.mdp.next.account;

// import com.mdp.next.entity.*;
// import com.mdp.next.repository.*;
// import com.mdp.next.service.AccountServiceImpl;
// import com.mdp.next.service.UserServiceImpl;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.MockitoJUnitRunner;

// @RunWith(MockitoJUnitRunner.class)
// public class AccountServiceTest {

//     @Mock
//     AccountRepository accountRepository;

//     @Mock
//     UserRepository userRepository;

//     @InjectMocks
//     AccountServiceImpl accountService;

//     @Test
//     public void testGetAllAccounts() {
//         when(accountRepository.findAll()).thenReturn(Arrays.asList( new Account(0.00), new Account(100.00), new Account(50.00) ));
//         List<Account> allAccounts = accountService.getAccounts();

//         assertEquals(allAccounts.size(), 3);
//         assertEquals(allAccounts.get(0).getBalance(), 0.00);
//         assertEquals(allAccounts.get(0).getSent(), null);
//         assertEquals(allAccounts.get(0).getUser(), null);
//     }

//     @Test
//     public void testGetAccountByUserID() {
//         Optional<User> user = Optional.of(new User(1L, null, "Mattia", "mattia@gmail.com", "123 Random Road"));
//         User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
//         Account account = new Account(10.00);
//         unwrappedUser.setAccount(account);
//         account.setUser(unwrappedUser);
//         account.setUserID(unwrappedUser.getID());

//         when(userRepository.findById(1L)).thenReturn(user);
//         Account fetchedAccount = accountService.getAccount(1L);
//         assertEquals(account, fetchedAccount);
//         assertEquals(account.getUserID(), 1);
//     }

//     @Test
//     public void testOpenAccount() {
//         Optional<User> user = Optional.of(new User(1L, null, "Mattia", "mattia@gmail.com", "123 Random Road"));
//         User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
//         when(userRepository.findById(1L)).thenReturn(user);
//         Account account = new Account(10.00);
//         accountService.openAccount(account, unwrappedUser.getID());
//         // verify that the user if first fetched, then the account opened, connected, and saved to the data store
//         verify(userRepository, times(1)).findById(unwrappedUser.getID());
//         verify(accountRepository, times(1)).save(account);
//     }

//     @Test 
//     public void testCloseAccount() {
//         Optional<User> user = Optional.of(new User(1L, null, "Mattia", "mattia@gmail.com", "123 Random Road"));
//         User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
//         when(userRepository.findById(1L)).thenReturn(user);
//         Account account = new Account(10.00);
//         accountService.openAccount(account, unwrappedUser.getID());

//         accountService.closeAccount(unwrappedUser.getID());
//         verify(accountRepository, times(1)).delete(account);
//     }


// }