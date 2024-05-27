package com.mdp.next.service;

import com.mdp.next.entity.User;
import java.util.List;

public interface UserService {

    User getUser(Long userID);
    List<User> getUsers();
    User createUser(User user);
    User updateUser(String newEmail, String newAddress, Long ID);
    void deleteUser(Long userID);

}
