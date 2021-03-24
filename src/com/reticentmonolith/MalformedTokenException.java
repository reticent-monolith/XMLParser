package com.reticentmonolith;

public class MalformedTokenException extends Exception {
    String token;
    public MalformedTokenException(String token) {
        super(token);
        this.token = token;
    }
}
