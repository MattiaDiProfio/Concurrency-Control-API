package com.mdp.next.security.filter;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdp.next.entity.User;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {

            // maps the request payload field to a user object - username, password
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            System.out.println("///////////////////////////////////////////////////");
            System.out.println(user.getUsername());
            System.out.println(user.getPassword());
            System.out.println("///////////////////////////////////////////////////");

            // pass the credentials into the authentication manager

        } catch (IOException e) {
            // exception handler will only work if the request is being handled by the dispatcher servlet, 
            // since this request is not ***, a runtime exception will not be caught hence why we gotta specify a 
            // custom filter exception handler
            // *** spring security filters are executed before the dispatcher servlet
            throw new RuntimeException();
        }
        return super.attemptAuthentication(request, response);
    }    

}
