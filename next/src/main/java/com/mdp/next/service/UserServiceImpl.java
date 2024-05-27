package com.mdp.next.service;

import java.util.List;
import java.util.Optional;
import com.mdp.next.entity.User;
import com.mdp.next.repository.UserRepository;
import com.mdp.next.exception.EntityNotFoundException;

public class UserServiceImpl implements UserService {

    UserRepository userRepository;    

    public User getUser(String userID) {
        return null;
    }

    public List<User> getUsers() {
        return null;
    }

    public User createUser(User user) {
        return null;
    }

    public User updateUser(User user, String ID) {
        return null;
    }

    public void deleteUser(String userID) {

    }

    static User unwrapUser(Optional<User> entity, String userID) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(userID, "user");
    }

}
