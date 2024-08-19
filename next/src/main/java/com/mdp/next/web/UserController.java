package com.mdp.next.web;

import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.mdp.next.service.UserService;
import com.mdp.next.entity.*;
import com.mdp.next.exception.UnauthorizedAccessException;
import com.mdp.next.exception.ErrorResponse;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getUsers() {

        // only admins can access this endpoint!
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserUsername = authentication.getPrincipal().toString();
        User user = userService.getUser(loggedInUserUsername);
        String role = user.getRole();

        if (!role.equals("ADMIN")) {
            throw new UnauthorizedAccessException("Only admins can access this endpoint!");
        }

        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userID}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userID) {

        // this object will never be null, since in order for this endpoint to be reached, the JWT filter will
        // have been triggered by now! hence, if we are here, its because SCH's authentication is not null!!
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // here we are checking that the logged in user is either an authorized ADMIN, or they are trying to
        // access resoruces tied to an account they own, and not someone else's!!!!

        // get the logged-in user's username by querying the security context holder
        String loggedInUserUsername = authentication.getPrincipal().toString();

        User user = userService.getUser(loggedInUserUsername);
        Long id = user.getUserId();
        String role = user.getRole();

        // the user is UNauthorised to get these resources!!!!
        if (id != userID && !role.equals("ADMIN")) {
            throw new UnauthorizedAccessException("Unauthorized access! You must be the resource owner or an admin to interact with this resource");
        } 

        // the user is either the owner or an admin, proceed with the request!
        return new ResponseEntity<>(userService.getUser(userID), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            result.getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage()));
            ErrorResponse error = new ErrorResponse(errors);
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{userID}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdateRequest user, BindingResult result, @PathVariable Long userID) {

        // check that the user is authorized to make this request!
        // only the account owner, not event admins, should be able to submit this request
        // only admins can access this endpoint!
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserUsername = authentication.getPrincipal().toString();
        User loggedInUser = userService.getUser(loggedInUserUsername);
        Long loggedInUserUserID = loggedInUser.getUserId();

        if (loggedInUserUserID != userID) {
            throw new UnauthorizedAccessException("Unauthorized access! Only the profile owner can access this resource.");
        }

        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            result.getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage())); 
            ErrorResponse error = new ErrorResponse(errors);  
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        userService.updateUser(user.getEmail(), user.getAddress(), userID);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long userID) {

        // check that the user is authorized to make this request!
        // only the account owner, not event admins, should be able to submit this request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserUsername = authentication.getPrincipal().toString();
        User loggedInUser = userService.getUser(loggedInUserUsername);
        Long loggedInUserUserID = loggedInUser.getUserId();

        if (loggedInUserUserID != userID) {
            throw new UnauthorizedAccessException("Unauthorized access! Only the profile owner can access this resource.");
        }

        userService.deleteUser(userID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
