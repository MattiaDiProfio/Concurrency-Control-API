package com.mdp.next;

import com.mdp.next.entity.User;
import com.mdp.next.repository.UserRepository;
import com.mdp.next.service.UserServiceImpl;
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
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
            new User("Mattia", "mattia@gmail.com", "1 Fake Road"), 
            new User("John", "john@email.com", "2 Common Street"), 
            new User("Mary", "mary@yahoo.com", "3 Random Avenue")
        ));

        List<User> allUsers = userService.getUsers();

        assertEquals(allUsers.size(), 3);
        assertEquals(allUsers.get(0).getName(), "Mattia");
        assertEquals(allUsers.get(2).getAccount(), null);
        assertEquals(allUsers.get(1).getEmail(), "john@email.com");
    }

    @Test
    public void testGetUserByID() {

        Optional<User> user = Optional.of(new User(1L, null, "Mattia", "mattia@email.com", "1 Common Street"));
        when(userRepository.findById(1L)).thenReturn(user);

        User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
        Long userID = unwrappedUser.getID();
        User fetchedUser = userService.getUser(userID);
        assertEquals(unwrappedUser, fetchedUser);

    }

    @Test
    public void testCreateUser() {
        // verify that the "createUser" method triggeres the repository's "save" method
        User newUser = new User("Mary", "mary@yahoo.com", "3 Random Avenue");
        userService.createUser(newUser);
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    public void testUpdateUser() {
        Optional<User> user = Optional.of(new User(1L, null, "Mattia", "mattia@email.com", "1 Common Street"));
        when(userRepository.findById(1L)).thenReturn(user);
        User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
        userService.updateUser(unwrappedUser.getEmail(), "2 Common Street", unwrappedUser.getID());
        verify(userRepository, times(1)).save(unwrappedUser);
    }

    @Test
    public void testDeleteUser() {
        Optional<User> user = Optional.of(new User(1L, null, "Mattia", "mattia@email.com", "1 Common Street"));
        when(userRepository.findById(1L)).thenReturn(user);
        User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
        userService.deleteUser(unwrappedUser.getID());
        verify(userRepository, times(1)).deleteById(unwrappedUser.getID());
    }

}
