package com.mdp.next.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationResponse {
    private String username;
    private String token;
}
