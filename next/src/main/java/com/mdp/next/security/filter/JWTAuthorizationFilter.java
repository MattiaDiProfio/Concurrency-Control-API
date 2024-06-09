package com.mdp.next.security.filter;

import java.io.IOException;
import java.util.Arrays;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mdp.next.security.SecurityConstants;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // header contains Bearer : JWT
        String header = request.getHeader("Authorization");

        // if header is null, we assume user is trying to signup
        // or if header doesnt start with BREARER there is no JWT to validate, hence go to next 
        // filter in the filter chain
        if (header == null || !header.startsWith(SecurityConstants.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        // fetch the jwt token from the request header
        String token = header.replace(SecurityConstants.BEARER, "");

        // TODO : check that the token is not expired 

        // validate jwt signature and check for a match
        String user = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
            .build() // verification builder "building" step
            .verify(token)
            .getSubject();

        // set the authentication object on the spring security context holder
        //                                                                  principal, credentials, authorities
        // if authentication yields false, the authentication manager will get called
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // hand over request to the dispatcher servlet
        filterChain.doFilter(request, response);
    }
}