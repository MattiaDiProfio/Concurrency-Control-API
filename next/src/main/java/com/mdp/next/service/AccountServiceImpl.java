package com.mdp.next.service;

import java.util.List;
import java.util.Optional;
import com.mdp.next.entity.*;
import com.mdp.next.exception.*;
import com.mdp.next.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;
    UserRepository userRepository;

    @Override
    public List<Account> getAccounts() {
        return (List<Account>) accountRepository.findAll();
    }

    @Override
    public Account getAccount(Long userID) {
        User user = UserServiceImpl.unwrapUser(userRepository.findById(userID), userID);
        Optional<Account> account = Optional.ofNullable(user.getAccount());
        return unwrapAccount(account, userID);
    }

    @Override
    public Account openAccount(Account account, Long userID) {
        // check that the user with userID exists 
        // and check that they do not have an open account yet 
        User user = UserServiceImpl.unwrapUser(userRepository.findById(userID), userID);
        if (Optional.ofNullable(user.getAccount()).isPresent()) throw new UserAccountUniquenessViolationException(userID);
        else {
            // the user exists and does not have an account, so we set the passed account
            account.setUser(user);
            account.setUserID(user.getID());
            user.setAccount(account);

            // NOTE for the future - altough it might seem intuitive to call userRepository.save(user) after updating the user information
            // doing so will actually create another account with userID of null, this probably occurs due to the fact that calling the setter
            // user.setAccount updates the user entity automatically.     

            /*
                StackOverflow post link - https://stackoverflow.com/questions/30388751/hibernate-updating-the-record-upon-calling-the-setter-methods-of-the-bean-class?rq=3
                "Persistent - Here the object is attached to the Hibernate session. So now the Hibernate session manages this object. 
                Any changes made to this object gets reflected in the database. Because Hibernate designed it in such way that, 
                if any modifications is made to a Persistent object, it automatically gets updated in the database,
                when the session is flushed. (This is Hibernate's capability)."
             */

            return accountRepository.save(account);
        }
    }

    @Override
    public Account updateAccount(Account account, Long userID) {
        return null;
    }

    @Override
    public void closeAccount(Long userID) {
        User user = UserServiceImpl.unwrapUser(userRepository.findById(userID), userID);
        Account account = unwrapAccount(Optional.ofNullable(user.getAccount()), userID);
        user.setAccount(null);
        accountRepository.delete(account);
    }

    public static Account unwrapAccount(Optional<Account> entity, Long userID) {
        if (entity.isPresent()) return entity.get();
        else throw new AccountNotFoundException(userID);
    }
    
}
