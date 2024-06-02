package com.mdp.next.security.manager;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import com.mdp.next.service.UserService;
import com.mdp.next.entity.User;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {
    
    private UserService userServiceImpl;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // the authentication object passed here comes from the Authentication filter class
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userServiceImpl.getUser(authentication.getName());

        if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("You provided an incorrect password.");
        }

        // this object indicates a password match and is returned to the Authentication Filter class
        return new UsernamePasswordAuthenticationToken(authentication.getName(), user.getPassword());
    }
}
