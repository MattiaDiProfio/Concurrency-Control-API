package com.mdp.next.security.filter;

import com.mdp.next.entity.User;
import com.mdp.next.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationFilter {

    private final UserService userService;

    public AuthorizationFilter(UserService userService) {
        this.userService = userService;
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserUsername = auth.getPrincipal().toString();
        User user = userService.getUser(loggedInUserUsername);
        return user.getRole().equals("ADMIN");
    }

    public boolean isOwner(Long loggedInUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserUsername = auth.getPrincipal().toString();
        User user = userService.getUser(loggedInUserUsername);
        return user.getUserId().equals(loggedInUserId);
    }

}
