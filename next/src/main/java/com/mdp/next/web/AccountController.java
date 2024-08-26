package com.mdp.next.web;

import com.mdp.next.entity.Account;
import com.mdp.next.exception.UnauthorizedAccessException;
import com.mdp.next.SecurityConstants;
import com.mdp.next.filter.AuthorizationFilter;
import com.mdp.next.service.AccountService;
import com.mdp.next.service.AccountServiceImpl;
import com.mdp.next.service.UserService;
import com.mdp.next.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final AuthorizationFilter authorizationFilter;

    public AccountController(AccountServiceImpl accountService, UserServiceImpl userService) {
        this.accountService = accountService;
        this.userService = userService;
        this.authorizationFilter = new AuthorizationFilter(userService);
    }

    @GetMapping("/user/{userID}/accounts/{accountID}")
    public ResponseEntity<Account> getAccount(@PathVariable Long userID, @PathVariable Long accountID) {
        if (
                !authorizationFilter.isOwner(userID) &&
                !authorizationFilter.isAdmin()
        ) throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_OR_ADMIN);
        return new ResponseEntity<>(accountService.getAccount(userID, accountID), HttpStatus.OK);
    }

    @GetMapping("/user/{userID}/accounts")
    public ResponseEntity<List<Account>> getUserAccounts(@PathVariable Long userID) {
        if (
                !authorizationFilter.isOwner(userID) &&
                !authorizationFilter.isAdmin()
        ) throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_OR_ADMIN);
        return new ResponseEntity<>(accountService.getUserAccounts(userID), HttpStatus.OK);
    }

    @GetMapping("/account/all")
    public ResponseEntity<List<Account>> getAllAccounts() {
        if (!authorizationFilter.isAdmin()) throw new UnauthorizedAccessException(SecurityConstants.ADMIN_ONLY);
        return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
    }

    @PostMapping("/user/{userID}/accounts")
    public ResponseEntity<?> openAccount(@Valid @RequestBody Account account, BindingResult result, @PathVariable Long userID) {
        if (authorizationFilter.isAdmin()) throw new UnauthorizedAccessException(SecurityConstants.NON_ADMIN_ONLY);
        if (!authorizationFilter.isOwner(userID)) throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_ONLY);

        // ensure that the account type is a valid option, specified in the Type custom validator exception
        if (result.hasErrors()) return UserController.extractPayloadErrors(result);
        accountService.openAccount(account, userID);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{userID}/accounts/{accountID}")
    public ResponseEntity<HttpStatus> closeAccount(@PathVariable Long userID, @PathVariable Long accountID) {
        if (!authorizationFilter.isOwner(userID)) throw new UnauthorizedAccessException(SecurityConstants.ACCOUNT_OWNER_ONLY);
        accountService.closeAccount(userID, accountID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
