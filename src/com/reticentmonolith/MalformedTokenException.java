package com.reticentmonolith;

public class MalformedTokenException extends Exception {
    public MalformedTokenException(String token) {
        super(token);
    }
}
