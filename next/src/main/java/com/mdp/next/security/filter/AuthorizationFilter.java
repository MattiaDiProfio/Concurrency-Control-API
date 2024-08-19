package com.mdp.next.security.filter;

import com.mdp.next.entity.User;
import com.mdp.next.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationFilter {

    private final Authentication authentication;
    private final UserService userService;

    public AuthorizationFilter(UserService userService) {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        this.userService = userService;
    }

    public boolean isAdmin() {
        String loggedInUserUsername = authentication.getPrincipal().toString();
        User user = userService.getUser(loggedInUserUsername);
        return user.getRole().equals("ADMIN");
    }

    public boolean isOwner(Long loggedInUserId) {
        String loggedInUserUsername = authentication.getPrincipal().toString();
        User user = userService.getUser(loggedInUserUsername);
        return user.getUserId().equals(loggedInUserId);
    }

}
