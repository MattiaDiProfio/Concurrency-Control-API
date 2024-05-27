package com.mdp.next.web;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.mdp.next.service.UserService;
import com.mdp.next.entity.*;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    @GetMapping("/{userID}")
    public ResponseEntity<User> getUser(@PathVariable Long userID) {
        return new ResponseEntity<>(userService.getUser(userID), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping("/{userID}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long userID) {
        return new ResponseEntity<>(userService.updateUser(user.getEmail(), user.getAddress(), userID), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userID}")
    public void deleteUser(@PathVariable Long userID) {
        userService.deleteUser(userID);
    }

}
