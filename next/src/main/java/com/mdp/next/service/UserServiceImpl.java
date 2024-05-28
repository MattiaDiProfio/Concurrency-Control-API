package com.mdp.next.service;

import java.util.List;
import java.util.Optional;

import com.mdp.next.entity.User;
import com.mdp.next.repository.*;
import com.mdp.next.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;   
    AccountRepository accountRepository; 

    @Override
    public User getUser(Long userID) {
        return unwrapUser(userRepository.findById(userID), userID);
    }

    @Override
    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User createUser(User user) { 
        return userRepository.save(user);
    }

    @Override
    public User updateUser(String newEmail, String newAddress, Long userID) {
        if (!userRepository.findById(userID).isPresent()) throw new UserNotFoundException(userID, "user");
        User user = unwrapUser(userRepository.findById(userID), userID);
        user.setEmail(newEmail);
        user.setAddress(newAddress);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userID) {
        if (!userRepository.findById(userID).isPresent()) throw new UserNotFoundException(userID, "user");
        else userRepository.deleteById(userID);
    }

    public static User unwrapUser(Optional<User> entity, Long userID) {
        if (entity.isPresent()) return entity.get();
        else throw new UserNotFoundException(userID, "user");
    }

}
