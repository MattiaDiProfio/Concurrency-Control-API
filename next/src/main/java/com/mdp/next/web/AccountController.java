package com.mdp.next.web;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.mdp.next.service.AccountService;
import com.mdp.next.entity.*;
import lombok.AllArgsConstructor;
import java.util.List;
import jakarta.validation.Valid;

@AllArgsConstructor
@RestController
public class AccountController {

    AccountService accountService;

    @GetMapping("/account/all")
    public ResponseEntity<List<Account>> getAccounts() {
        return new ResponseEntity<>(accountService.getAccounts(), HttpStatus.OK);
    }

    @GetMapping("/user/{userID}/account")
    public ResponseEntity<Account> getAccount(@PathVariable Long userID) {
        return new ResponseEntity<>(accountService.getAccount(userID), HttpStatus.OK);
    }

    @PostMapping("/user/{userID}/account")
    public ResponseEntity<Account> openAccount(@Valid @RequestBody Account account, @PathVariable Long userID) {
        return new ResponseEntity<>(accountService.openAccount(account, userID), HttpStatus.CREATED);
    }

    @PutMapping("/user/{userID}/account")
    public ResponseEntity<Account> updateAccount(@Valid @RequestBody Account account, @PathVariable Long userID) { // THINK OF WHAT PARAMETERS TO BE PASSED INTO THIS METHOD
        return null;
    }

    @DeleteMapping("/user/{userID}/account") 
    public ResponseEntity<HttpStatus> closeAccount(@PathVariable Long userID) {
        return null;
    }

}
