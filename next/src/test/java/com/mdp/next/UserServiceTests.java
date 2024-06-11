package com.mdp.next;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.mdp.next.entity.User;
import com.mdp.next.entity.UserDTO;
import com.mdp.next.repository.UserRepository;
import com.mdp.next.service.UserServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTests {
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getGradesFromRepoTest() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
            new User("mattia", "mdp@gmail.com", "123 street", "mattia123", "pass123", "ADMIN")
        ));
        List<UserDTO> result = userService.getUsers();
        assertEquals("mattia", result.get(0).getName());
    }

} 