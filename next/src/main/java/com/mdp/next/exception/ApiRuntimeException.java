package com.mdp.next.exception;

public class ApiRuntimeException extends RuntimeException {

    public ApiRuntimeException(String message, Object... args) {
        super(args.length > 0 ? String.format(message, args) : message);
    }

}
