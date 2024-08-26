package com.mdp.next.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.mdp.next.entity.User;
import com.mdp.next.entity.UserDTO;
import com.mdp.next.repository.UserRepository;
import com.mdp.next.service.UserServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testGetUserById() {
        Optional<User> user = Optional.of(new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "ADMIN"));
        when(userRepository.findById(1L)).thenReturn(user);

        User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
        UserDTO fetchedUser = userService.getUser(1L);

        assertEquals(unwrappedUser.getUserId(), fetchedUser.getID());
        assertEquals(unwrappedUser.getName(), fetchedUser.getName());
        assertEquals(unwrappedUser.getEmail(), fetchedUser.getEmail());
        assertEquals(unwrappedUser.getUsername(), fetchedUser.getUsername());
    }

    @Test
    public void testGetUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "ADMIN"),
                new User("John", "john@email.com", "2 Common Street", "john456", "password456", "CUSTOMER"),
                new User("Mary", "mary@yahoo.com", "3 Random Avenue", "mary789", "password789", "CUSTOMER")
        ));

        List<UserDTO> allUsers = userService.getUsers();

        assertEquals(allUsers.size(), 3);
        assertEquals(allUsers.get(0).getName(), "Mattia");
        assertEquals(allUsers.get(1).getEmail(), "john@email.com");
    }

    @Test
    public void testCreateUser() {
        // verify that the "createUser" method triggers the repository's "save" method
        User newUser = new User("Mattia", "mattia@gmail.com", "123 Road Avenue", "mattia123", "password123", "ADMIN");
        when(bCryptPasswordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword123");
        userService.createUser(newUser);
        assertEquals(newUser.getPassword(), "encodedPassword123");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    public void testGetUserByUsername() {
        Optional<User> user = Optional.of(new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "ADMIN"));
        when(userRepository.findByUsername("mattia123")).thenReturn(user);

        User unwrappedUser = UserServiceImpl.unwrapUser(user, 404L);
        User fetchedUser = userService.getUser("mattia123");

        assertNull(fetchedUser.getUserId()); // the id returned by this method is null by default
        assertEquals(unwrappedUser.getName(), fetchedUser.getName());
        assertEquals(unwrappedUser.getEmail(), fetchedUser.getEmail());
        assertEquals(unwrappedUser.getUsername(), fetchedUser.getUsername());
    }

    @Test
    public void testUpdateUser() {
        Optional<User> user = Optional.of(new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "ADMIN"));
        when(userRepository.findById(1L)).thenReturn(user);
        User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
        userService.updateUser(unwrappedUser.getEmail(), "2 Common Street", 1L);
        verify(userRepository, times(1)).save(unwrappedUser);
    }

    @Test
    public void testDeleteUser() {
        Optional<User> user = Optional.of(new User("Mattia", "mattia@gmail.com", "1 Fake Road", "mattia123", "password123", "ADMIN"));
        when(userRepository.findById(1L)).thenReturn(user);
        User unwrappedUser = UserServiceImpl.unwrapUser(user, 1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

}