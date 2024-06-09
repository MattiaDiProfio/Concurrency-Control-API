package com.mdp.next.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Expired JWT");
    }
}
