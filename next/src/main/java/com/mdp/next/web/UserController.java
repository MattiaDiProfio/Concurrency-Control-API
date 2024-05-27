package com.mdp.next.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mdp.next.service.UserService;
import com.mdp.next.entity.User;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    @GetMapping("/{userID}")
    public ResponseEntity<User> getUser(@PathVariable String userID) {
        return null;
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers() {
        return null;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return null;
    }

    @PutMapping("/{userID}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable String userID) {
        return null;
    }

    @DeleteMapping("/{userID}")
    public void deleteUser(@PathVariable String userID) {

    }

}
