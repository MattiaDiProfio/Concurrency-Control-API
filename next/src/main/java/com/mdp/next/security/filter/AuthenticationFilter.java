package com.mdp.next.security.filter;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Date;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdp.next.security.SecurityConstants;
import com.mdp.next.security.manager.CustomAuthenticationManager;
import com.mdp.next.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import lombok.AllArgsConstructor;
import com.mdp.next.entity.*;
import com.mdp.next.repository.*;

@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private CustomAuthenticationManager authenticationManager;  
    private UserService userService;
    private TokenRepository tokenRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {

            // maps the request payload field to a user object - username, password
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            // pass the credentials into the authentication manager using an authentication object
            //                                                                  principal = username, credentials = password
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            return authenticationManager.authenticate(authentication);

        } catch (IOException e) {
            // exception handler will only work if the request is being handled by the dispatcher servlet, 
            // since this request is not ***, a runtime exception will not be caught hence why we gotta specify a 
            // custom filter exception handler
            // *** spring security filters are executed before the dispatcher servlet
            throw new RuntimeException();
        }
    }    

    // method triggered when the attemptAuthentication method fails 
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(failed.getMessage());
        response.getWriter().flush();
    }

    // method triggered when the attemptAuthentication method succedes
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String token = JWT.create()
            .withSubject(authResult.getName())
            .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
            .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

        // save the token into a Token instance 
        // connect the token to the user 
        User requestUser = userService.getUser(authResult.getName());
        Token tokenDTO = new Token();
        tokenDTO.setUser(requestUser);
        tokenDTO.setActive(true);
        tokenRepository.save(tokenDTO);

        response.addHeader(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

        // include the username and token in the response object 
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(new AuthenticationResponse(authResult.getName(), token)));
    }

}
