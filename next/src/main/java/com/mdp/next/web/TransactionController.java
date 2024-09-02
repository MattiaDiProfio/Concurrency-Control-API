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

    private final TransactionService transactionService;
    private final AuthorizationFilter authorizationFilter;

    public TransactionController(TransactionServiceImpl transactionService, UserServiceImpl userService) {
        this.transactionService = transactionService;
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

    @PostMapping("/user/{userID}/accounts/{accountID}/place")
    public ResponseEntity<?> placeTransaction(@RequestBody Transaction transaction, @PathVariable Long userID, @PathVariable Long accountID) {
        if (authorizationFilter.isAdmin()) throw new UnauthorizedAccessException(SecurityConstants.NON_ADMIN_ONLY);
        if (!authorizationFilter.isOwner(userID)) throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_ONLY);
        transactionService.placeTransaction(transaction, userID, accountID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
