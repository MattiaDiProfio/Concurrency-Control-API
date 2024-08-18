package com.mdp.next.security.manager;

import com.mdp.next.entity.Token;
import com.mdp.next.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Cannot logout before login");
                response.getWriter().flush();
                return;
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        String token = authHeader.substring(7);

        Token storedToken = tokenRepository.findByBody(token).orElse(null);

        // check that storedToken is not null - this can occur if the JWT provided is a random string input 
        // and not an actual or previously valid token!
        if (storedToken != null && !storedToken.isActive()) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Cannot logout using an expired token");
                response.getWriter().flush();
                return;
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        if (token != null) {
            storedToken.setActive(false);
            tokenRepository.save(storedToken);
            try {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                response.getWriter().write("logout successfull");
                response.getWriter().flush();
                return;
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } 
    }
}