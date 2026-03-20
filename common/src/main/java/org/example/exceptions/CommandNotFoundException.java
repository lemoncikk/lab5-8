package org.example.exceptions;

public class CommandNotFoundException extends AppException {
    public CommandNotFoundException(String message) {
        super(message);
    }
}
