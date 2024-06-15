package com.mdp.next.web;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.mdp.next.service.*;
import com.mdp.next.entity.*;
import lombok.AllArgsConstructor;
import java.util.List;
import jakarta.validation.Valid;

@AllArgsConstructor
@RestController
public class TransactionController {
    
    TransactionService transactionService;

    @GetMapping("/transaction/all")
    public ResponseEntity<List<Transaction>> getTransactions() {
        return new ResponseEntity<>(transactionService.getTransactions(), HttpStatus.OK);
    }

    @GetMapping("/user/{userID}/account/transaction/sent")
    public ResponseEntity<List<Transaction>> getAccountSentTransactions(@PathVariable Long userID) {
        return new ResponseEntity<>(transactionService.getAccountSentTransactions(userID), HttpStatus.OK);
    }       

    @GetMapping("/user/{userID}/account/transaction/received")
    public ResponseEntity<List<Transaction>> getAccountReceivedTransactions(@PathVariable Long userID) {
        return new ResponseEntity<>(transactionService.getAccountReceivedTransactions(userID), HttpStatus.OK);
    }     

    @PostMapping("/transaction") 
    public ResponseEntity<Transaction> placeTransaction(@Valid @RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.placeTransaction(transaction), HttpStatus.CREATED);
    }

}
