package com.mdp.next.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.mdp.next.repository.TokenRepository;
import com.mdp.next.security.filter.AuthenticationFilter;
import com.mdp.next.security.filter.JWTAuthorizationFilter;
import com.mdp.next.security.manager.CustomAuthenticationManager;
import com.mdp.next.service.UserService;
import com.mdp.next.security.filter.ExceptionHandlerFilter;
import lombok.AllArgsConstructor;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    CustomAuthenticationManager authenticationManager;
    private UserService userService;
    private TokenRepository tokenRepository;

    @SuppressWarnings({ "deprecation", "removal" })
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // change the url on which the attemptAuthenticate() method on the authenticationFilter 
        // will be triggered from "/login" (default) to "/authenticate"
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, userService, tokenRepository);
        authenticationFilter.setFilterProcessesUrl("/authenticate"); 

        http
            .headers(headers -> headers.frameOptions().disable())
            .csrf(csrf -> csrf.disable())
            .authorizeRequests(requests -> requests
                    .requestMatchers("/h2/**").permitAll() // New Line: allows us to access the h2 console without the need to authenticate. ' ** '  instead of ' * ' because multiple path levels will follow /h2.
                    .requestMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH).permitAll()
                    .anyRequest().authenticated())
                    .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class) // runs before all other filters, setting up a global exception handler for exception uncatchable by the dispatcher servlet
                    .addFilter(authenticationFilter)
                    .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}