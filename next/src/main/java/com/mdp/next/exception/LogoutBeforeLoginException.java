package com.mdp.next.exception;

public class LogoutBeforeLoginException extends RuntimeException {
    public LogoutBeforeLoginException() {
        super("Cannot logout without being logged in");
    }
}
