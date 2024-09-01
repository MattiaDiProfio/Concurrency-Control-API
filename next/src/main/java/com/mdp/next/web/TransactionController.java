package com.mdp.next.web;

import com.mdp.next.SecurityConstants;
import com.mdp.next.entity.Transaction;
import com.mdp.next.exception.UnauthorizedAccessException;
import com.mdp.next.filter.AuthorizationFilter;
import com.mdp.next.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class TransactionController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AuthorizationFilter authorizationFilter;

    public TransactionController(TransactionServiceImpl transactionService, AccountServiceImpl accountService, UserServiceImpl userService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.authorizationFilter = new AuthorizationFilter(userService);
    }

    @GetMapping("/user/{userID}/accounts/{accountID}/sent")
    public ResponseEntity<List<Transaction>> getAccountSentTransactions(@PathVariable Long userID, @PathVariable Long accountID) {
        if (!authorizationFilter.isOwner(userID) && !authorizationFilter.isAdmin()) {
            throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_OR_ADMIN);
        }
        return new ResponseEntity<>(transactionService.getSentTransactions(userID, accountID), HttpStatus.OK);
    }

    @GetMapping("/user/{userID}/accounts/{accountID}/received")
    public ResponseEntity<List<Transaction>> getAccountReceivedTransactions(@PathVariable Long userID, @PathVariable Long accountID) {
        if (!authorizationFilter.isOwner(userID) && !authorizationFilter.isAdmin()) {
            throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_OR_ADMIN);
        }
        return new ResponseEntity<>(transactionService.getReceivedTransactions(userID, accountID), HttpStatus.OK);
    }

    @GetMapping("/transaction/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        if (!authorizationFilter.isAdmin()) {
            throw new UnauthorizedAccessException(SecurityConstants.ADMIN_ONLY);
        }
        return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> placeTransaction(@Valid @RequestBody Transaction transaction) {
        // define a custom annotation to check that the transaction is valid
        // we can do the following checks in the service layer!
        // namely, the amount specified is > 0.0
        // the account and sender cannot be the same, and they must both be valid (existing)
        // the sender account must have enough funds to cover the transaction
        // in the service layer, trigger the OCC algorithm
        return null;
    }

}
