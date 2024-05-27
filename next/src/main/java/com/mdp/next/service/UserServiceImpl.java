package com.mdp.next.service;

import java.util.List;
import java.util.Optional;
import com.mdp.next.entity.User;
import com.mdp.next.repository.UserRepository;
import com.mdp.next.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;    

    public User getUser(String userID) {
        return unwrapUser(userRepository.findById(userID), userID);
    }

    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(String newEmail, String newAddress, String userID) {
        User user = unwrapUser(userRepository.findById(userID), userID);
        user.setEmail(newEmail);
        user.setAddress(newAddress);
        return userRepository.save(user);
    }

    public void deleteUser(String userID) {
        userRepository.deleteById(userID);
    }

    static User unwrapUser(Optional<User> entity, String userID) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(userID, "user");
    }

}
