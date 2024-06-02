package com.mdp.next.service;

import com.mdp.next.entity.User;
import com.mdp.next.entity.UserDTO;
import java.util.List;

public interface UserService {

    UserDTO getUser(Long userID);
    User getUser(String username);
    List<UserDTO> getUsers();
    User createUser(User user);
    User updateUser(String newEmail, String newAddress, Long ID);
    void deleteUser(Long userID);

}
