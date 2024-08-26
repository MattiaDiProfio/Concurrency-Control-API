package com.mdp.next.web;

import com.mdp.next.security.SecurityConstants;
import com.mdp.next.security.filter.AuthorizationFilter;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.mdp.next.service.UserService;
import com.mdp.next.entity.*;
import com.mdp.next.exception.UnauthorizedAccessException;
import com.mdp.next.exception.ErrorResponse;
import java.util.List;
import java.util.ArrayList;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthorizationFilter authorizationFilter;

    public UserController(UserService userService) {
        this.userService = userService;
        this.authorizationFilter = new AuthorizationFilter(userService);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getUsers() {
        if (!authorizationFilter.isAdmin()) throw new UnauthorizedAccessException(SecurityConstants.ADMIN_ONLY);
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userID}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userID) {
        if (
                !authorizationFilter.isOwner(userID) &&
                !authorizationFilter.isAdmin()
        ) throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_OR_ADMIN);
        return new ResponseEntity<>(userService.getUser(userID), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) return extractPayloadErrors(result);
        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{userID}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateRequest user, BindingResult result, @PathVariable Long userID) {
        if (!authorizationFilter.isOwner(userID)) throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_ONLY);
        if (result.hasErrors()) return extractPayloadErrors(result);
        userService.updateUser(user.getEmail(), user.getAddress(), userID);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long userID) {
        if (!authorizationFilter.isOwner(userID)) throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_ONLY);
        userService.deleteUser(userID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity<ErrorResponse> extractPayloadErrors(BindingResult result) {
        List<String> errors = new ArrayList<>();
        result.getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage()));
        ErrorResponse error = new ErrorResponse(errors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
