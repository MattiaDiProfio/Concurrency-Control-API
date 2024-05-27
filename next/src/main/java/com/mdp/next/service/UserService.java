package com.mdp.next.service;

import com.mdp.next.entity.User;
import java.util.List;

public interface UserService {

    User getUser(String userID);
    List<User> getUsers();
    User createUser(User user);
    User updateUser(User user, String ID);
    void deleteUser(String userID);

}
